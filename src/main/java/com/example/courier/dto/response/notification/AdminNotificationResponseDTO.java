package com.example.courier.dto.response.notification;

import com.example.courier.dto.NotificationBase;
import com.example.courier.service.notification.NotificationTarget;

import java.time.LocalDateTime;

public interface AdminNotificationResponseDTO extends NotificationBase {
        Long getId();
        String getTitle();
        String getMessage();
        LocalDateTime getCreatedAt();
        //NotificationTarget target
}