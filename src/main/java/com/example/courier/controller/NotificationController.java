package com.example.courier.controller;

import com.example.courier.common.NotificationTargetType;
import com.example.courier.domain.Notification;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.request.NotificationMessage;
import com.example.courier.dto.request.notification.NotificationRequestDTO;
import com.example.courier.dto.response.notification.NotificationResponseDTO;
import com.example.courier.repository.NotificationRepository;
import com.example.courier.service.notification.NotificationService;
import com.example.courier.service.notification.NotificationTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/getAdminNotifications")
    public List<Notification> notifyAdmin() {
        return notificationRepository.findAll();
    }

 /*   @PostMapping("create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO> create(NotificationMessage notificationMessage) {
        notificationServiceImpl.broadcastToType();
    }*/

    @PostMapping("/test")
    public ResponseEntity<?> testNotification() {
        notificationService.createNotification(
                new NotificationRequestDTO(
                        "test",
                        "test notification content, long long text. lets see how it goes.. " +
                                "addding even more text. lets seee how it goes... even more... super long text " +
                                " to fetch on ui.... lets seee... alsmost done... enough for this time.... ",
                        new NotificationTarget.BroadCast(NotificationTargetType.COURIER))
        );
        return ResponseEntity.ok("Notification sent");
    }

    @PostMapping("/markAsRead")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURIER', 'USER')")
    public ResponseEntity<ApiResponseDTO> markAsRead(@RequestBody List<Long> ids) {
        return ResponseEntity.ok(notificationService.markAsRead(ids));
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'COURIER', 'USER')")
    public ResponseEntity<PaginatedResponseDTO<NotificationResponseDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(notificationService.getNotificationHistory(pageable));
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURIER', 'USER')")
    public ResponseEntity<ApiResponseDTO> delete(@RequestBody List<Long> ids) {
        return ResponseEntity.ok(notificationService.delete(ids));
    }
}
