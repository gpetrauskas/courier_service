package com.example.courier.controller;

import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.request.notification.NotificationRequestDTO;
import com.example.courier.dto.response.notification.AdminNotificationResponseDTO;
import com.example.courier.dto.response.notification.NotificationResponseDTO;
import com.example.courier.service.notification.NotificationService;
import com.example.courier.validation.shared.NotNullOrEmpty;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody @Valid NotificationRequestDTO requestDTO) {
        return ResponseEntity.ok(notificationService.createNotification(requestDTO));
    }

    @PostMapping("/markAsRead")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURIER', 'USER')")
    public ResponseEntity<ApiResponseDTO> markAsRead(@RequestBody @NotNullOrEmpty List<Long> ids) {
        return ResponseEntity.ok(notificationService.markAsRead(ids));
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'COURIER', 'USER')")
    public ResponseEntity<PaginatedResponseDTO<NotificationResponseDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(notificationService.getNotificationHistory(pageable));
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURIER', 'USER')")
    public ResponseEntity<ApiResponseDTO> delete(@RequestBody @NotNullOrEmpty List<Long> ids) {
        return ResponseEntity.ok(notificationService.delete(ids));
    }

    @GetMapping("/adminManage")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedResponseDTO<AdminNotificationResponseDTO>> getAllForAdmin(Pageable pageable) {
        return ResponseEntity.ok(notificationService.getAllForAdmin(pageable));
    }

    @GetMapping("/page-containing/{notificationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURIER', 'USER')")
    public ResponseEntity<PaginatedResponseDTO<NotificationResponseDTO>> getPageContainingNotification(
            @PathVariable Long notificationId,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return ResponseEntity.ok(notificationService.getPageContainingNotification(notificationId, pageSize));
    }
}
