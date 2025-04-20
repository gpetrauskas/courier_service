package com.example.courier.dto.mapper;

import com.example.courier.domain.Notification;
import com.example.courier.dto.response.notification.AdminNotificationResponseDTO;
import com.example.courier.dto.response.notification.NotificationResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NotificationMapper {

    NotificationResponseDTO toDTO(Notification notification);

    AdminNotificationResponseDTO toAdminDTO(Notification notification);
}
