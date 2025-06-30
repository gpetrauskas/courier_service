package com.example.courier.service.notification;

import com.example.courier.common.ApiResponseType;
import com.example.courier.domain.*;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.mapper.NotificationMapper;
import com.example.courier.dto.request.notification.NotificationRequestDTO;
import com.example.courier.dto.response.notification.AdminNotificationResponseDTO;
import com.example.courier.dto.response.notification.NotificationResponseDTO;
import com.example.courier.dto.response.notification.NotificationWithReadStatus;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.repository.NotificationRepository;
import com.example.courier.repository.PersonNotificationRepository;
import com.example.courier.service.notification.strategy.NotificationDeliveryStrategy;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.util.AuthUtils;
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

    public PaginatedResponseDTO<NotificationResponseDTO> getNotificationHistory(Pageable pageable) {
        return getNotificationsPaginated(pageable);
    }

    @Transactional
    public ApiResponseDTO markAsRead(List<Long> ids) {
        final Long personId = currentPersonService.getCurrentPersonId();
        return (ids.size() > 1)
                ? markMultipleNotificationsAsRead(personId, ids)
                : markSingleNotificationAsRead(personId, ids.getFirst());
    }

    @Transactional
    public ApiResponseDTO delete(List<Long> ids) {
        if (currentPersonService.isAdmin()) {
            return adminDelete(ids);
        }

        final Long personId = currentPersonService.getCurrentPersonId();
        return (ids.size() > 1)
                ? deleteMultipleNotifications(personId, ids)
                : deleteSingleNotification(personId, ids.getFirst());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PaginatedResponseDTO<AdminNotificationResponseDTO> getAllForAdmin(Pageable pageable) {
        boolean isAdmin = AuthUtils.isAdmin();
        if (!isAdmin) {
            throw new UnauthorizedAccessException("Unauthorized access");
        }

        Page<Notification> pNList = notificationRepository.findAll(pageable);

        List<AdminNotificationResponseDTO> dtoList = pNList.stream()
                .map(notificationMapper::toAdminDTO)
                .toList();

        return new PaginatedResponseDTO<>(
                dtoList,
                pageable.getPageNumber(),
                pNList.getTotalElements(),
                pNList.getTotalPages()
        );
    }

    public PaginatedResponseDTO<NotificationResponseDTO> getPageContainingNotification(Long notificationId, int pageSize) {
        Long personId = AuthUtils.getAuthenticatedPersonId();

        List<Long> notifications = personNotificationRepository.findNotificationIdsByPerson(personId);
        int index = notifications.indexOf(notificationId);
        if (index == -1) {
            throw new ResourceNotFoundException("Notification not found.");
        }

        int pageIndex = index / pageSize;
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by("notification.createdAt").descending());
        Page<NotificationWithReadStatus> dto = notificationRepository.findAllByRecipientIdPageable(personId, pageable);
        List<NotificationResponseDTO> dtoList = dto.getContent()
                .stream()
                .map(n ->
                        new NotificationResponseDTO(
                                n.getId(),
                                n.getTitle(),
                                n.getMessage(),
                                n.getCreatedAt(),
                                n.getReadAt(),
                                n.getIsRead()
                        ))
                .toList();

        return new PaginatedResponseDTO<>(
                dtoList,
                pageable.getPageNumber(),
                dto.getTotalElements(),
                dto.getTotalPages());
    }

    public PaginatedResponseDTO<NotificationResponseDTO> getNotificationsPaginated(Pageable pageable) {
        Page<NotificationWithReadStatus> notificationPage = notificationRepository
                .findAllByRecipientIdPageable(currentPersonService.getCurrentPersonId(), pageable);

        List<NotificationResponseDTO> content = notificationPage.getContent()
                .stream()
                .map(notificationMapper::toDTO)
                .toList();

        return new PaginatedResponseDTO<>(
                content,
                notificationPage.getNumber(),
                notificationPage.getTotalElements(),
                notificationPage.getTotalPages()
        );
    }

    private ApiResponseDTO markMultipleNotificationsAsRead(Long personId, List<Long> notificationsIds) {
        int updatedRows = personNotificationRepository.markMultipleAsRead(personId, notificationsIds, LocalDateTime.now());

        return (updatedRows > 0)
                ? ApiResponseType.MULTIPLE_NOTIFICATIONS_MARK_AS_READ_SUCCESS.withParams(updatedRows, notificationsIds.size())
                : ApiResponseType.MULTIPLE_NOTIFICATIONS_MARK_AS_READ_INFO.apiResponseDTO();
    }

    private ApiResponseDTO markSingleNotificationAsRead(Long personId, Long notificationId) {
        PersonNotification pn = personNotificationRepository.findByIdAndPersonId(notificationId, personId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification was ot found"));

        if (pn.isRead()) {
            return ApiResponseType.SINGLE_NOTIFICATION_MARK_AS_READ_INFO.apiResponseDTO();
        }

        pn.markAsRead();
        return ApiResponseType.SINGLE_NOTIFICATION_MARK_AS_READ_SUCCESS.apiResponseDTO();
    }

    private ApiResponseDTO deleteMultipleNotifications(Long personId, List<Long> ids) {
        int deletedRows = personNotificationRepository.deleteMultipleByIdAndPersonId(ids, personId);
        if (deletedRows > 0) {
            return ApiResponseType.MULTIPLE_NOTIFICATIONS_DELETE_SUCCESS.withParams(deletedRows, ids.size());
        } else {
            return ApiResponseType.MULTIPLE_NOTIFICATIONS_DELEte_INFO.apiResponseDTO();
        }
    }

    private ApiResponseDTO deleteSingleNotification(Long personId, Long notificationId) {
        int deletedRow = personNotificationRepository.deleteByNotificationIdAndPersonId(notificationId, personId);
        if (deletedRow == 1) {
            return ApiResponseType.SINGLE_NOTIFICATION_DELETE_SUCCESS.apiResponseDTO();
        } else {
            return ApiResponseType.SINGLE_NOTIFICATION_DELETE_INFO.apiResponseDTO();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    private ApiResponseDTO adminDelete(List<Long> ids) {
        personNotificationRepository.deleteAllByNotificationIdIn(ids);
        notificationRepository.deleteAllById(ids);
        return ApiResponseType.SINGLE_NOTIFICATION_DELETE_SUCCESS.apiResponseDTO();
    }
}
