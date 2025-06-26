package com.example.courier.notificationservicetests;

import com.example.courier.common.NotificationTargetType;
import com.example.courier.domain.Notification;
import com.example.courier.domain.User;
import com.example.courier.dto.mapper.NotificationMapper;
import com.example.courier.dto.request.notification.NotificationRequestDTO;
import com.example.courier.repository.NotificationRepository;
import com.example.courier.repository.PersonNotificationRepository;
import com.example.courier.service.notification.NotificationServiceImpl;
import com.example.courier.service.notification.NotificationTarget;
import com.example.courier.service.person.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private final List<Long> listOfUserIds = List.of(1L, 2L);

    private NotificationRequestDTO createTestNotificationRequestDTO(NotificationTarget target) {
        return new NotificationRequestDTO("test title", "test message", target);
    }

    @BeforeEach
    void setUp() {
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocationOnMock -> {
                    Notification notification = invocationOnMock.getArgument(0);
                    notification.setId(99L);
                    return notification;
                });
    }

    @Nested
    class CreateNotification {
        @Test
        @DisplayName("success broadcast ")
        void success_broadcast() {
            NotificationRequestDTO requestDTO = createTestNotificationRequestDTO(
                    new NotificationTarget.BroadCast(NotificationTargetType.USER));

            when(personService.findAllActiveIdsByType(User.class)).thenReturn(listOfUserIds);

            notificationService.createNotification(requestDTO);

            verify(personService).findAllActiveIdsByType(User.class);
            verify(notificationRepository).save(any(Notification.class));
            verify(personNotificationRepository).bulkInsert(99L, listOfUserIds);
            verify(notificationRepository).save(argThat(notification ->
                    notification.getCreatedAt() != null));
        }

        @Test
        @DisplayName("success individual ")
        void success_individual() {
            NotificationRequestDTO requestDTO = createTestNotificationRequestDTO(
                    new NotificationTarget.Individual(1L)
            );

            notificationService.createNotification(requestDTO);

            verify(notificationRepository).save(any(Notification.class));
            verify(personNotificationRepository).bulkInsert(99L, List.of(1L));
            verify(personService, never()).findAllActiveIdsByType(any());
        }


    }
}
