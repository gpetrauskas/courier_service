package com.example.courier.service.notification;

import com.example.courier.common.ApiResponseType;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.mapper.NotificationMapper;
import com.example.courier.dto.request.notification.NotificationRequestDTO;
import com.example.courier.dto.response.notification.AdminNotificationResponseDTO;
import com.example.courier.dto.response.notification.NotificationResponseDTO;
import com.example.courier.dto.response.notification.NotificationWithReadStatus;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.NotificationRepository;
import com.example.courier.repository.PersonNotificationRepository;
import com.example.courier.service.notification.strategy.NotificationDeliveryStrategy;
import com.example.courier.service.security.CurrentPersonService;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final NotificationRepository notificationRepository;
    private final PersonNotificationRepository personNotificationRepository;
    private final NotificationMapper notificationMapper;
    private final CurrentPersonService currentPersonService;
    private final List<NotificationDeliveryStrategy> strategies;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository, PersonNotificationRepository personNotificationRepository, NotificationMapper notificationMapper,
            CurrentPersonService currentPersonService, List<NotificationDeliveryStrategy> strategies) {
        this.notificationRepository = notificationRepository;
        this.personNotificationRepository = personNotificationRepository;
        this.notificationMapper = notificationMapper;
        this.currentPersonService = currentPersonService;
        this.strategies = strategies;
    }

    @Override
    @Transactional
    public ApiResponseDTO createNotification(NotificationRequestDTO request) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(request.type()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported notification type"))
                .deliver(request);
    }

    public List<NotificationResponseDTO> getUnreadNotifications(Long personId) {
        // for later
        return List.of();
    }

    @Transactional
    public ApiResponseDTO markAsRead(List<Long> ids) {
        final Long personId = currentPersonService.getCurrentPersonId();
        int updatedRows = personNotificationRepository.markMultipleAsRead(personId, ids, LocalDateTime.now());

        return (updatedRows > 0)
                ? ApiResponseType.MULTIPLE_NOTIFICATIONS_MARK_AS_READ_SUCCESS.withParams(updatedRows, ids.size())
                : ApiResponseType.MULTIPLE_NOTIFICATIONS_MARK_AS_READ_INFO.apiResponseDTO();
    }

    public ApiResponseDTO delete(List<Long> ids) {
        return currentPersonService.isAdmin()
                ? adminDelete(ids)
                : userDelete(ids);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    private ApiResponseDTO adminDelete(List<Long> ids) {
        personNotificationRepository.deleteAllByNotificationIdIn(ids);
        notificationRepository.deleteAllById(ids);
        return ApiResponseType.NOTIFICATIONS_DELETE_SUCCESS_ADMIN.apiResponseDTO();
    }

    @Transactional
    private ApiResponseDTO userDelete(List<Long> ids) {
        final Long personId = currentPersonService.getCurrentPersonId();
        int deletedRows = personNotificationRepository.deleteMultipleByIdAndPersonId(ids, personId);

        return (deletedRows > 0)
                ? ApiResponseType.NOTIFICATIONS_DELETE_SUCCESS.withParams(deletedRows, ids.size())
                : ApiResponseType.NOTIFICATIONS_DELETE_INFO.apiResponseDTO();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PaginatedResponseDTO<AdminNotificationResponseDTO> getAllForAdmin(Pageable pageable) {
        Page<AdminNotificationResponseDTO> page = notificationRepository.findAllProjectedBy(pageable);

        return new PaginatedResponseDTO<>(page, Function.identity());
    }

    public PaginatedResponseDTO<NotificationResponseDTO> getPageContainingNotification(Long notificationId, int pageSize) {
        Long personId = currentPersonService.getCurrentPersonId();

        int position = personNotificationRepository.findNotificationPosition(personId, notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        Pageable pageable = pageForItemIndex(position, pageSize);
        Page<NotificationWithReadStatus> page = notificationRepository.findAllByRecipientIdPageable(personId, pageable);

        return mapToDTO(page);
    }

    private Pageable pageForItemIndex(int index, int size) {
        return PageRequest.of(index / size, size, Sort.by("notification.createdAt").descending());
    }

    public PaginatedResponseDTO<NotificationResponseDTO> getNotificationHistory(@NotNull Pageable pageable) {
        Long personId = currentPersonService.getCurrentPersonId();
        Page<NotificationWithReadStatus> notificationPage = notificationRepository
                .findAllByRecipientIdPageable(personId, pageable);

        return mapToDTO(notificationPage);
    }

    private PaginatedResponseDTO<NotificationResponseDTO> mapToDTO(Page<NotificationWithReadStatus> page) {
        return new PaginatedResponseDTO<>(page, notificationMapper::toDTO);
    }
}