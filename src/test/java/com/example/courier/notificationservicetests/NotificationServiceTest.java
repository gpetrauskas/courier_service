package com.example.courier.notificationservicetests;

import com.example.courier.common.NotificationTargetType;
import com.example.courier.domain.Courier;
import com.example.courier.domain.Notification;
import com.example.courier.domain.PersonNotification;
import com.example.courier.domain.User;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.mapper.NotificationMapper;
import com.example.courier.dto.request.notification.NotificationRequestDTO;
import com.example.courier.dto.response.notification.NotificationResponseDTO;
import com.example.courier.dto.response.notification.NotificationWithReadStatus;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.repository.NotificationRepository;
import com.example.courier.repository.PersonNotificationRepository;
import com.example.courier.service.notification.NotificationServiceImpl;
import com.example.courier.service.notification.NotificationTarget;
import com.example.courier.service.notification.strategy.BroadcastNotificationStrategy;
import com.example.courier.service.notification.strategy.IndividualNotificationStrategy;
import com.example.courier.service.notification.strategy.NotificationDeliveryStrategy;
import com.example.courier.service.person.PersonService;
import com.example.courier.service.security.CurrentPersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private PersonService personService;
    @Mock
    private PersonNotificationRepository personNotificationRepository;
    @Mock
    private NotificationMapper notificationMapper;
    @Mock
    private CurrentPersonService currentPersonService;

    @Mock
    private IndividualNotificationStrategy individual;
    @Mock
    private BroadcastNotificationStrategy broadcast;

    private NotificationServiceImpl notificationService;

    @BeforeEach
    void init() {
        List<NotificationDeliveryStrategy> strategies = List.of(individual, broadcast);
        notificationService = new NotificationServiceImpl(
                notificationRepository,
                personNotificationRepository,
                notificationMapper,
                currentPersonService,
                strategies
        );
    }

    private final List<Long> listOfUserIds = List.of(1L, 2L);
    private Pageable pageable = PageRequest.of(0, 10);

    private NotificationRequestDTO createTestNotificationRequestDTO(NotificationTarget target) {
        return new NotificationRequestDTO("test title", "test message", target);
    }

    @Nested
    class CreateNotification {
        @BeforeEach
        void setUp() {
            lenient().when(notificationRepository.save(any(Notification.class)))
                    .thenAnswer(invocationOnMock -> {
                        Notification notification = invocationOnMock.getArgument(0);
                        notification.setId(99L);
                        return notification;
                    });
        }

        @Test
        @DisplayName("success broadcast to class")
        void success_broadcast() {
            NotificationRequestDTO requestDTO = createTestNotificationRequestDTO(
                    new NotificationTarget.BroadCast(NotificationTargetType.USER));

            when(broadcast.supports(any())).thenReturn(true);
            when(broadcast.deliver(any())).thenReturn(new ApiResponseDTO("success", "sent"));

            ApiResponseDTO responseDTO = notificationService.createNotification(requestDTO);

            verify(broadcast).deliver(requestDTO);
            assertEquals("success", responseDTO.status());
        }

        @Test
        @DisplayName("success individual")
        void success_individual() {
            NotificationRequestDTO requestDTO = createTestNotificationRequestDTO(
                    new NotificationTarget.Individual(1L)
            );

            when(individual.supports(any())).thenReturn(true);
            when(individual.deliver(any())).thenReturn(new ApiResponseDTO("success", "Sent"));

            ApiResponseDTO responseDTO = notificationService.createNotification(requestDTO);

            verify(individual).deliver(requestDTO);
            verifyNoInteractions(broadcast);
            assertEquals("success", responseDTO.status());
        }

        @Test
        @DisplayName("throws when null person ID in individual notification")
        void individual_nullPersonId() {
            NotificationRequestDTO request = createTestNotificationRequestDTO(
                    new NotificationTarget.Individual(null)
            );

            assertThrows(IllegalArgumentException.class, () -> notificationService.createNotification(request));
        }

        @Test
        @DisplayName("return warning response - empty ids list")
        void emptyIdsList_returnWarningResponse() {
            NotificationRequestDTO requestDTO = createTestNotificationRequestDTO(
                    new NotificationTarget.BroadCast(NotificationTargetType.COURIER)
            );

            when(broadcast.supports(any())).thenReturn(true);
            when(broadcast.deliver(requestDTO)).thenReturn(new ApiResponseDTO("warning", "empty list"));

            ApiResponseDTO responseDTO = notificationService.createNotification(requestDTO);

            verify(broadcast).deliver(requestDTO);
            verify(individual).supports(requestDTO.type());
            verifyNoMoreInteractions(broadcast);
            assertEquals("warning", responseDTO.status());
        }
    }

    @Nested
    class GetNotificationsHistory {
        private List<NotificationWithReadStatus> createTestNotificationWIthReadStatus() {
            List<NotificationWithReadStatus> list = new ArrayList<>();

            for (int i = 0; i < 12; i ++) {
                int id = i;
                list.add(new NotificationWithReadStatus() {
                    @Override public Long getId() { return (long) id; }
                    @Override public String getTitle() { return "Test Title " + id; }
                    @Override public String getMessage() { return "Test Message " + id; }
                    @Override public LocalDateTime getCreatedAt() { return LocalDateTime.now(); }
                    @Override public LocalDateTime getReadAt() { return null; }
                    @Override public Boolean getIsRead() { return false; }
                });
            }
            return list;
        }


        @Test
        @DisplayName("success - get notifications history")
        void success() {
            List<NotificationWithReadStatus> notifications = createTestNotificationWIthReadStatus();
            Page<NotificationWithReadStatus> notificationPage = new PageImpl<>(
                    notifications.subList(0, 10), pageable, notifications.size()
            );

            when(currentPersonService.getCurrentPersonId()).thenReturn(1L);
            when(notificationRepository.findAllByRecipientIdPageable(1L, pageable)).thenReturn(notificationPage);

            PaginatedResponseDTO<NotificationResponseDTO> responseDTO = notificationService.getNotificationsPaginated(pageable);

            assertEquals(12, responseDTO.totalItems());
            assertEquals(10, responseDTO.data().size());
            assertEquals(0, responseDTO.currentPage());
            assertEquals(2, responseDTO.totalPages());
        }
    }

    @Nested
    class MarkAsRead {
        private List<Long> notificationIds = List.of(1L, 2L, 3L, 4L);
        private Notification notification;
        private User user;
        private PersonNotification pn;

        @BeforeEach
        void setup() {
            lenient().when(currentPersonService.getCurrentPersonId()).thenReturn(1L);

            notification = new Notification("test title", "test message", LocalDateTime.now());
            user = new User();
            pn = new PersonNotification(user, notification);
        }

        @Test
        @DisplayName("mark as read multiple notifications")
        void multipleNotifications() {
            when(personNotificationRepository.markMultipleAsRead(eq(1L), eq(notificationIds), any(LocalDateTime.class))).thenReturn(4);

            ApiResponseDTO responseDTO = notificationService.markAsRead(notificationIds);

            assertEquals("Marked 4 of 4 notifications as read", responseDTO.message());
        }

        @Test
        @DisplayName("info response - nothing was updated")
        void multipleUpdate_nothingUpdated() {
            when(personNotificationRepository.markMultipleAsRead(eq(1L), eq(notificationIds), any(LocalDateTime.class))).thenReturn(0);

            ApiResponseDTO responseDTO = notificationService.markAsRead(notificationIds);

            assertEquals("No notifications were updated. Please check the IDs", responseDTO.message());
        }

        @Test
        @DisplayName("mark as read single notification")
        void singleNotification() {
            List<Long> singleNotificationList = List.of(1L);

            when(personNotificationRepository.findByIdAndPersonId(singleNotificationList.getFirst(), 1L)).thenReturn(Optional.of(pn));

            ApiResponseDTO responseDTO = notificationService.markAsRead(singleNotificationList);

            assertEquals("Notification marked as read successfully", responseDTO.message());
        }

        @Test
        @DisplayName("info response - single notification already isRead")
        void singleNotification_alreadyIsRead() {
            pn.markAsRead();

            when(personNotificationRepository.findByIdAndPersonId(listOfUserIds.getFirst(), 1L)).thenReturn(Optional.of(pn));

            ApiResponseDTO responseDTO = notificationService.markAsRead(List.of(listOfUserIds.getFirst()));

            assertEquals("Notification already marked as read", responseDTO.message());
        }

        @Test
        @DisplayName("PersonNotification not found - throws")
        void personNotificationNotFound_throw() {
            when(personNotificationRepository.findByIdAndPersonId(listOfUserIds.getFirst(), 1L))
                    .thenThrow(new ResourceNotFoundException("Notification was not found"));

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                    notificationService.markAsRead(List.of(listOfUserIds.getFirst())));

            assertEquals("Notification was not found", ex.getMessage());
        }
    }

    @Nested
    class delete {
        @Test
        @DisplayName("success is admin")
        void success_isAdmin() {
            when(currentPersonService.isAdmin()).thenReturn(true);

            ApiResponseDTO responseDTO = notificationService.delete(List.of(1L));

            assertEquals("Notification was deleted successfully", responseDTO.message());
        }

        @Test
        @DisplayName("success single notification")
        void success_singleNotification() {
            when(currentPersonService.isAdmin()).thenReturn(false);
            when(currentPersonService.getCurrentPersonId()).thenReturn(1L);
            when(personNotificationRepository.deleteByNotificationIdAndPersonId(1L, 1L)).thenReturn(1);

            ApiResponseDTO responseDTO = notificationService.delete(List.of(1L));

            assertEquals("success", responseDTO.status());
            assertEquals("Notification was deleted successfully", responseDTO.message());
            verify(personNotificationRepository).deleteByNotificationIdAndPersonId(anyLong(), anyLong());
        }
    }
}
