package com.example.courier.service.notification;

import com.example.courier.common.ApiResponseType;
import com.example.courier.common.NotificationTargetType;
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
import com.example.courier.service.person.PersonService;
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
    private final PersonService personService;
    private final PersonNotificationRepository personNotificationRepository;
    private final NotificationMapper notificationMapper;
    private final CurrentPersonService currentPersonService;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository, PersonService personService,
            PersonNotificationRepository personNotificationRepository, NotificationMapper notificationMapper,
            CurrentPersonService currentPersonService) {
        this.notificationRepository = notificationRepository;
        this.personService = personService;
        this.personNotificationRepository = personNotificationRepository;
        this.notificationMapper = notificationMapper;
        this.currentPersonService = currentPersonService;
    }

    @Override
    @Transactional
    public ApiResponseDTO createNotification(NotificationRequestDTO request) {
        return switch (request.type()) {
            case NotificationTarget.BroadCast broadcast -> handleBroadCast(request, broadcast);
            case NotificationTarget.Individual individual -> handleIndividual(request, individual);
        };
    }

    private Class<? extends Person> getPersonClass(NotificationTargetType type) {
        return switch (type) {
            case COURIER -> Courier.class;
            case ADMIN -> Admin.class;
            case USER -> User.class;
        };
    }

    private ApiResponseDTO handleIndividual(NotificationRequestDTO request, NotificationTarget.Individual individual) {
        Long personId = individual.personId();

        if (personId == null) {
            throw new IllegalArgumentException("Person ID cannot be null for individual notification");
        }
        createNotificationWithRecipients(request, List.of(personId));
        log.info("Sending individual notification to ID {}", personId);
        return new ApiResponseDTO("success", "Notification sent to person with id " + personId);
    }

    @Override
    public List<NotificationResponseDTO> getUnreadNotifications(Long personId) {
        // for later
        return List.of();
    }

    @Override
    public PaginatedResponseDTO<NotificationResponseDTO> getNotificationHistory(Pageable pageable) {
        return getNotificationsPaginated(pageable);
    }

    @Override
    @Transactional
    public ApiResponseDTO markAsRead(List<Long> ids) {
        if (ids.isEmpty()) {
            throw new IllegalArgumentException("Notification list cannot be empty");
        }

        final Long personId = currentPersonService.getCurrentPersonId();
        if (ids.size() > 1) {
            return markMultipleNotificationsAsRead(personId, ids);
        } else {
            return markSingleNotificationAsRead(personId, ids.getFirst());
        }
    }

    @Override
    @Transactional
    public ApiResponseDTO delete(List<Long> ids) {
        if (ids.isEmpty()) {
            throw new IllegalArgumentException("Notifications List cannot be empty");
        }

        if (AuthUtils.isAdmin()) {
            return adminDelete(ids);
        }

        final Long personId = AuthUtils.getAuthenticatedPersonId();
        return ids.size() > 1 ?
                deleteMultipleNotifications(personId, ids)
                :
                deleteSingleNotification(personId, ids.get(0));
    }

    @Override
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

    @Override
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

    private ApiResponseDTO handleBroadCast(NotificationRequestDTO message, NotificationTarget.BroadCast broadCast) {
        Class<? extends Person> personClass = getPersonClass(broadCast.type());
        List<Long> recipients = personService.findAllActiveIdsByType(personClass);

        if (recipients.isEmpty()) {
            return new ApiResponseDTO("warning", "No recipients found for " + personClass.getSimpleName());
        }

        createNotificationWithRecipients(message, recipients);
        log.info("Broadcasting to {} recipient(s) of type {}", recipients.size(), personClass.getSimpleName());
        return new ApiResponseDTO("success", "Notification sent to " + recipients.size() + " " + personClass.getSimpleName() + "(s)");
    }

    @Transactional
    private void createNotificationWithRecipients(NotificationRequestDTO message, List<Long> recipients) {
        Notification notification = new Notification(
                message.title(),
                message.message(),
                LocalDateTime.now()
        );
        notificationRepository.save(notification);
        personNotificationRepository.bulkInsert(notification.getId(), recipients);
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
        boolean isAdmin = AuthUtils.isAdmin();
        if (isAdmin) {
            personNotificationRepository.deleteAllByNotificationIdIn(ids);
            notificationRepository.deleteAllById(ids);
            return ApiResponseType.SINGLE_NOTIFICATION_DELETE_SUCCESS.apiResponseDTO();
        } else {
            throw new UnauthorizedAccessException("Unauthorized access");
        }
    }
}
