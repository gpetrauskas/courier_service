package com.example.courier.service.notification;

import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.request.notification.NotificationRequestDTO;
import com.example.courier.dto.response.notification.AdminNotificationResponseDTO;
import com.example.courier.dto.response.notification.NotificationResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface fir managing notifications
 */
public interface NotificationService {

    /**
     * Creates a new notification using the appropriate delivery strategy.
     *
     * @param request the notification creation request.
     * @return an {@link ApiResponseDTO} result of the operation. */
    ApiResponseDTO createNotification(NotificationRequestDTO request);

    /**
     * Sends notification to admins that a courier has returned from a task and successfully checked in.
     *
     * @param taskId the id of the task that was completed
     * @param courierId the id of returned courier
     */
    void notifyCourierCheckedIn(Long taskId, Long courierId);

    /**
     */
    List<NotificationResponseDTO> getUnreadNotifications(Long personId);

    /**
     * Retrieves paginated history of notifications for the current user.
     *
     * @param pageable pagination and sorting information.
     * @return a {@link PaginatedResponseDTO<NotificationResponseDTO>} containing users notification data.
     */
    PaginatedResponseDTO<NotificationResponseDTO> getHistoryForCurrentUser(Pageable pageable);

    /**
     * Marks the specified notifications.
     *
     * @param ids the list of notification ids to mark.
     * @return a {@link ApiResponseDTO} result of the operation
     */
    ApiResponseDTO markAsRead(List<Long> ids);

    /**
     * Deletes the specified notifications.
     *
     * @param ids the list of notifications to delete.
     * @return {@link ApiResponseDTO} the result of the operation.
     */
    ApiResponseDTO deleteNotifications(List<Long> ids);

    /**
     * Retrieves paginated list of all notifications for amin.
     *
     * @param pageable pagination and sorting information.
     * @return a {@link PaginatedResponseDTO<AdminNotificationResponseDTO>} containing admin level notification data.
     */
    PaginatedResponseDTO<AdminNotificationResponseDTO> getAllForAdmin(Pageable pageable);

    /**
     * Retrieves the page of notifications that contains the specified notification ID
     * for the current user.
     *
     * @param notificationId the ID of notification to locate.
     * @param pageSize size of notifications per page.
     * @return a {@link PaginatedResponseDTO<NotificationResponseDTO>} containing targeted notification.
     */
    PaginatedResponseDTO<NotificationResponseDTO> getPageContainingNotification(Long notificationId, int pageSize);
}
