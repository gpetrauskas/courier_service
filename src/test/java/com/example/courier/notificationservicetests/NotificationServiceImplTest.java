package com.example.courier.notificationservicetests;

import com.example.courier.common.NotificationTargetType;
import com.example.courier.domain.Notification;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.mapper.NotificationMapper;
import com.example.courier.dto.request.notification.NotificationRequestDTO;
import com.example.courier.dto.response.notification.AdminNotificationResponseDTO;
import com.example.courier.dto.response.notification.NotificationResponseDTO;
import com.example.courier.dto.response.notification.NotificationWithReadStatus;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.NotificationRepository;
import com.example.courier.repository.PersonNotificationRepository;
import com.example.courier.service.notification.NotificationServiceImpl;
import com.example.courier.service.notification.NotificationTarget;
import com.example.courier.service.notification.strategy.BroadcastNotificationStrategy;
import com.example.courier.service.notification.strategy.IndividualNotificationStrategy;
import com.example.courier.service.notification.strategy.NotificationDeliveryStrategy;
import com.example.courier.service.security.CurrentPersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;
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
            return IntStream.range(0, 12)
                    .mapToObj(i -> mock(NotificationWithReadStatus.class))
                    .toList();
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

            PaginatedResponseDTO<NotificationResponseDTO> responseDTO = notificationService.getNotificationHistory(pageable);

            assertEquals(12, responseDTO.totalItems());
            assertEquals(10, responseDTO.data().size());
            assertEquals(0, responseDTO.currentPage());
            assertEquals(2, responseDTO.totalPages());
        }
    }

    @Nested
    class MarkAsRead {
        private final List<Long> notificationIds = List.of(1L, 2L, 3L, 4L);

        @BeforeEach
        void setup() {
            lenient().when(currentPersonService.getCurrentPersonId()).thenReturn(1L);
        }

        @Test
        @DisplayName("mark as read multiple notifications")
        void multipleNotifications() {
            when(personNotificationRepository.markMultipleAsRead(anyLong(), eq(notificationIds), any(LocalDateTime.class))).thenReturn(4);

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
            when(personNotificationRepository.markMultipleAsRead(eq(notificationIds.getFirst()), anyList(), any(LocalDateTime.class))).thenReturn(1);

            ApiResponseDTO responseDTO = notificationService.markAsRead(List.of(notificationIds.getFirst()));

            assertEquals("Marked 1 of 1 notifications as read", responseDTO.message());
        }

        @Test
        @DisplayName("info response - single notification already isRead")
        void singleNotification_alreadyIsRead() {
            when(personNotificationRepository.markMultipleAsRead(anyLong(), anyList(), any())).thenReturn(0);

            ApiResponseDTO responseDTO = notificationService.markAsRead(List.of(listOfUserIds.getFirst()));

            assertEquals("No notifications were updated. Please check the IDs", responseDTO.message());
        }

        @Test
        @DisplayName("PersonNotification not found - throws")
        void personNotificationNotFound_throw() {
            when(personNotificationRepository.markMultipleAsRead(eq(listOfUserIds.getFirst()), anyList(), any(LocalDateTime.class)))
                    .thenReturn(0);

            ApiResponseDTO responseDTO = notificationService.markAsRead(List.of(listOfUserIds.getFirst()));

            assertEquals("No notifications were updated. Please check the IDs", responseDTO.message());
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
            when(personNotificationRepository.deleteMultipleByIdAndPersonId(List.of(1L), 1L)).thenReturn(1);

            ApiResponseDTO responseDTO = notificationService.delete(List.of(1L));

            assertEquals("success", responseDTO.status());
            assertEquals("Deleted 1 of 1 notifications", responseDTO.message());
            verify(personNotificationRepository).deleteMultipleByIdAndPersonId(anyList(), anyLong());
        }
    }

    @Nested
    class GetAllForAdmin {
        @Test
        @DisplayName("success")
        void success() {
            List<AdminNotificationResponseDTO> dtos = List.of(mock(AdminNotificationResponseDTO.class));
            Page<AdminNotificationResponseDTO> page = new PageImpl<>(dtos);

            when(notificationRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(page);

            PaginatedResponseDTO<AdminNotificationResponseDTO> responseDTO =
                    notificationService.getAllForAdmin(PageRequest.of(0, 10));

            assertEquals(1, responseDTO.data().size());
            assertEquals(1, responseDTO.totalPages());
            assertEquals(1, responseDTO.totalItems());
            assertEquals(0, responseDTO.currentPage());
        }

        @Test
        @DisplayName("returns empty paginated response")
        void shouldReturnEmptyResponse() {
            Page<AdminNotificationResponseDTO> emptypage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(notificationRepository.findAllProjectedBy(pageable)).thenReturn(emptypage);

            PaginatedResponseDTO<AdminNotificationResponseDTO> responseDTO = notificationService.getAllForAdmin(pageable);

            assertNotNull(responseDTO);
            assertTrue(responseDTO.data().isEmpty());
            assertEquals(0, responseDTO.totalPages());
            assertEquals(0, responseDTO.currentPage());
            assertEquals(0, responseDTO.totalItems());
        }
    }

    @Nested
    class GetPageContainingNotification {
        @Test
        @DisplayName("success")
        void returnPageContainingNotification_success() {
            Long personId = 1L;
            Long notificationId = 1L;
            int notificationIndex = 7;
            int pageSize = 3;
            Pageable pageRequest = PageRequest.of(notificationIndex / pageSize, pageSize, Sort.by("notification.createdAt").descending());
            List<NotificationWithReadStatus> mockContent = List.of(mock(NotificationWithReadStatus.class));
            Page<NotificationWithReadStatus> mockPage = new PageImpl<>(mockContent, pageRequest, 20);

            when(currentPersonService.getCurrentPersonId()).thenReturn(personId);
            when(personNotificationRepository.findNotificationPosition(personId, notificationId)).thenReturn(Optional.of(notificationIndex));
            when(notificationRepository.findAllByRecipientIdPageable(personId, pageRequest)).thenReturn(mockPage);

            PaginatedResponseDTO<NotificationResponseDTO> responseDTO = notificationService.getPageContainingNotification(notificationId, pageSize);

            assertNotNull(responseDTO);
        }

        @Test
        @DisplayName("person notification not found")
        void throws_PersonNotificationNotFound() {
            when(currentPersonService.getCurrentPersonId()).thenReturn(1L);
            when(personNotificationRepository.findNotificationPosition(1L, 1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () ->
                    notificationService.getPageContainingNotification(1L, 5));
        }
    }
}
