package com.example.courier.service.notification;

import com.example.courier.common.ApiResponseType;
import com.example.courier.common.NotificationTargetType;
import com.example.courier.domain.*;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.mapper.NotificationMapper;
import com.example.courier.dto.request.notification.NotificationRequestDTO;
import com.example.courier.dto.response.notification.NotificationResponseDTO;
import com.example.courier.dto.response.notification.NotificationWithReadStatus;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.NotificationRepository;
import com.example.courier.repository.PersonNotificationRepository;
import com.example.courier.service.person.PersonService;
import com.example.courier.util.AuthUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final PersonService personService;
    private final PersonNotificationRepository personNotificationRepository;


    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   PersonService personService, PersonNotificationRepository personNotificationRepository) {
        this.notificationRepository = notificationRepository;
        this.personService = personService;
        this.personNotificationRepository = personNotificationRepository;
    }

    @Override
    @Transactional
    public void createNotification(NotificationRequestDTO request) {
        switch (request.type()) {
            case NotificationTarget.BroadCast broadcast ->
                    broadcastToType(getPersonClass(broadcast.type()), request);
            case NotificationTarget.Individual individual ->
                    sendToPerson(request, individual.personId());
        }
    }

    private Class<? extends Person> getPersonClass(NotificationTargetType type) {
        return switch (type) {
            case COURIER -> Courier.class;
            case ADMIN -> Admin.class;
            case USER -> User.class;
        };
    }

    @Override
    public void sendToPerson(NotificationRequestDTO request, Long personId) {
        createNotificationWithRecipients(request, List.of(personId));
    }

    @Override
    public List<NotificationResponseDTO> getUnreadNotifications(Long personId) {
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

        final Long personId = AuthUtils.getAuthenticatedPersonId();
        if (ids.size() > 1) {
            return markMultipleNotificationsAsRead(personId, ids);
        } else {
            return markSingleNotificationAsRead(personId, ids.get(0));
        }
    }

    @Override
    @Transactional
    public ApiResponseDTO delete(Long notificationId) {
        Long personId = AuthUtils.getAuthenticatedPersonId();

        if (notificationId != null) {
            personNotificationRepository.deleteByNotificationIdAndPersonId(notificationId, personId);
            return new ApiResponseDTO("success", "Notification successfully deleted");
        } else {
            Long deletedCount = personNotificationRepository.deleteAllByPersonId(personId);
            if (deletedCount > 0) {
                return new ApiResponseDTO("success", "Successfully deleted " + deletedCount + " notifications");
            }
            return new ApiResponseDTO("info", "No notifications to be deleted");
        }
    }

    private void broadcastToType(Class<? extends Person> personClass, NotificationRequestDTO message) {
        List<Long> recipients = personService.findAllActiveIdsByType(personClass);
        System.out.println(recipients);
        System.out.println("test " + personClass.getSimpleName());
        createNotificationWithRecipients(message, recipients);
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
        Long recipientId = AuthUtils.getAuthenticatedPersonId();

        Page<NotificationWithReadStatus> notificationPage = notificationRepository.findAllByRecipientIdPageable(recipientId, pageable);

        List<NotificationResponseDTO> content = notificationPage.getContent()
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
                content,
                pageable.getPageNumber(),
                notificationPage.getTotalElements(),
                notificationPage.getTotalPages()
        );
    }

    private ApiResponseDTO markMultipleNotificationsAsRead(Long personId, List<Long> notificationsIds) {
        int updatedRows = personNotificationRepository.markMultipleAsRead(
                personId,
                notificationsIds,
                LocalDateTime.now()
        );

        if (updatedRows > 0) {
            return ApiResponseType.MULTIPLE_NOTIFICATIONS_MARK_AS_READ_SUCCESS.withParams(updatedRows, notificationsIds.size());
        } else {
            return ApiResponseType.MULTIPLE_NOTIFICATIONS_MARK_AS_READ_INFO.apiResponseDTO();
        }
    }

    private ApiResponseDTO markSingleNotificationAsRead(Long personId, Long notificationId) {
        PersonNotification personNotification = personNotificationRepository.findByIdAndPersonId(notificationId, personId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification was ot found"));
        if (personNotification.isRead()) {
            return ApiResponseType.SINGLE_NOTIFICATION_MARK_AS_READ_INFO.apiResponseDTO();
        } else {
            personNotification.markAsRead();
            return ApiResponseType.SINGLE_NOTIFICATION_MARK_AS_READ_SUCCESS.apiResponseDTO();
        }
    }
}
