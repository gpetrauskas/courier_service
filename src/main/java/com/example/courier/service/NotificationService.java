package com.example.courier.service;

import com.example.courier.domain.Notification;
import com.example.courier.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void notifyAdmin(Long taskId, Long courierId) {
        String message = String.format("Courier ID = %d has checked in for Task ID = %d", courierId, taskId);

        Notification notification = new Notification(message, LocalDateTime.now());
        notificationRepository.save(notification);
    }

}
