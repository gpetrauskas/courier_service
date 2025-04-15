package com.example.courier.dto.response.notification;

import java.time.LocalDateTime;

public interface NotificationWithReadStatus {
    Long getId();
    String getTitle();
    String getMessage();
    LocalDateTime getCreatedAt();
    LocalDateTime getReadAt();
    Boolean getIsRead();
}
