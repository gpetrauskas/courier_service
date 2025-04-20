package com.example.courier.dto.response.notification;

import com.example.courier.dto.NotificationBase;

import java.time.LocalDateTime;

public interface NotificationWithReadStatus extends NotificationBase {
    LocalDateTime getReadAt();
    Boolean getIsRead();
}
