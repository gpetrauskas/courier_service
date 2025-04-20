package com.example.courier.dto;

import java.time.LocalDateTime;

public interface NotificationBase {
    Long getId();
    String getTitle();
    String getMessage();
    LocalDateTime getCreatedAt();
}
