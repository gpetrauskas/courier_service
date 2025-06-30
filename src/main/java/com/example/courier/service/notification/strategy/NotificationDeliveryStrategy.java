package com.example.courier.service.notification.strategy;

import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.notification.NotificationRequestDTO;
import com.example.courier.service.notification.NotificationTarget;

public interface NotificationDeliveryStrategy {
    boolean supports(NotificationTarget target);
    ApiResponseDTO deliver(NotificationRequestDTO requestDTO);
}
