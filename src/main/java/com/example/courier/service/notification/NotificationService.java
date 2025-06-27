package com.example.courier.service.notification;

import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.request.notification.NotificationRequestDTO;
import com.example.courier.dto.response.notification.AdminNotificationResponseDTO;
import com.example.courier.dto.response.notification.NotificationResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    ApiResponseDTO createNotification(NotificationRequestDTO request);
    //ApiResponseDTO handleIndividual(NotificationRequestDTO message, NotificationTarget.Individual individual);
    List<NotificationResponseDTO> getUnreadNotifications(Long personId);
    PaginatedResponseDTO<NotificationResponseDTO> getNotificationHistory(Pageable pageable);
    ApiResponseDTO markAsRead(List<Long> ids);
    ApiResponseDTO delete(List<Long> ids);
    PaginatedResponseDTO<AdminNotificationResponseDTO> getAllForAdmin(Pageable pageable);
    PaginatedResponseDTO<NotificationResponseDTO> getPageContainingNotification(Long notificationId, int pageSize);
}
