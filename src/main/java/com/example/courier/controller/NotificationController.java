package com.example.courier.controller;

import com.example.courier.domain.Courier;
import com.example.courier.domain.Notification;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.request.NotificationMessage;
import com.example.courier.dto.response.notification.NotificationResponseDTO;
import com.example.courier.repository.NotificationRepository;
import com.example.courier.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
        notificationService.broadcastToType();
    }*/

    @PostMapping("/test")
    public ResponseEntity<?> testNotification() {
        notificationService.broadcastToType(
                Courier.class   ,
                new NotificationMessage("test", "test notification content")
        );
        return ResponseEntity.ok("Notification sent");
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'COURIER', 'USER')")
    public ResponseEntity<PaginatedResponseDTO<NotificationResponseDTO>>getAll(
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(notificationService.getNotificationsPaginated(size, page));
    }
}
