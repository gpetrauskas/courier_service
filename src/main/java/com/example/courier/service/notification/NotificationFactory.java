package com.example.courier.service.notification;


import com.example.courier.domain.Notification;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.repository.NotificationRepository;
import com.example.courier.repository.PersonNotificationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class NotificationFactory {

    private final NotificationRepository notificationRepository;
    private final PersonNotificationRepository personNotificationRepository;

    public NotificationFactory(NotificationRepository notificationRepository,
                                          PersonNotificationRepository personNotificationRepository) {
        this.notificationRepository = notificationRepository;
        this.personNotificationRepository = personNotificationRepository;
    }

    @Transactional
    public ApiResponseDTO createNotification(String title, String message, List<Long> idsBatch) {
        Notification notification = new Notification(title, message, LocalDateTime.now());

        notificationRepository.save(notification);
        personNotificationRepository.bulkInsert(notification.getId(), idsBatch);

        return new ApiResponseDTO("success", "Notification sent to " + idsBatch.size() + " user(s)");
    }
}
