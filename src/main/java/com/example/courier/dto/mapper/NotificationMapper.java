package com.example.courier.dto.mapper;

import com.example.courier.domain.Notification;
import com.example.courier.dto.response.notification.AdminNotificationResponseDTO;
import com.example.courier.dto.response.notification.NotificationResponseDTO;
import com.example.courier.dto.response.notification.NotificationWithReadStatus;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NotificationMapper {

    NotificationResponseDTO toDTO(Notification notification);
    NotificationResponseDTO toDTO(NotificationWithReadStatus notification);

    AdminNotificationResponseDTO toAdminDTO(Notification notification);
}
