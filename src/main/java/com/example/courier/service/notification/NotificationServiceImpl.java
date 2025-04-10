package com.example.courier.service.notification;

import com.example.courier.common.NotificationTargetType;
import com.example.courier.domain.*;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.mapper.NotificationMapper;
import com.example.courier.dto.request.notification.NotificationRequestDTO;
import com.example.courier.dto.response.notification.NotificationResponseDTO;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.NotificationRepository;
import com.example.courier.service.person.PersonService;
import com.example.courier.util.AuthUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final PersonService personService;
    private final NotificationMapper notificationMapper;


    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   PersonService personService, NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.personService = personService;
        this.notificationMapper = notificationMapper;
    }

    @Override
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
        Person person = personService.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person was not found"));
        createNotificationWithRecipients(request, Collections.singleton(person));
    }

    @Override
    public List<NotificationResponseDTO> getUnreadNotifications(Long personId) {
        return List.of();
    }

    @Override
    public PaginatedResponseDTO<NotificationResponseDTO> getNotificationHistory(Pageable pageable) {
        return getNotificationsPaginated(pageable);

    }

    private void broadcastToType(Class<? extends Person> personClass, NotificationRequestDTO message) {
        List<? extends Person> recipients = personService.getAllActiveByType(personClass);
        createNotificationWithRecipients(message, recipients);
    }

    private void createNotificationWithRecipients(NotificationRequestDTO message, Collection<? extends Person> recipients) {
        Notification notification = new Notification(
                message.title(),
                message.message(),
                LocalDateTime.now()
        );
        notification.setRecipients(new HashSet<>(recipients));
        notificationRepository.save(notification);
    }

    public PaginatedResponseDTO<NotificationResponseDTO> getNotificationsPaginated(Pageable pageable) {
        Long recipientId = AuthUtils.getAuthenticatedPersonId();

        Page<Notification> notificationPage = notificationRepository.findAllByRecipientIdPageable(recipientId, pageable);

        List<NotificationResponseDTO> content = notificationPage.getContent()
                .stream()
                .map(notificationMapper::toDTO)
                .toList();

        return new PaginatedResponseDTO<>(
                content,
                pageable.getPageNumber(),
                notificationPage.getTotalElements(),
                notificationPage.getTotalPages()
        );
    }
}
