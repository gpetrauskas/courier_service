package com.example.courier.service.notification;

import com.example.courier.domain.Admin;
import com.example.courier.domain.Notification;
import com.example.courier.domain.Person;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.request.NotificationMessage;
import com.example.courier.dto.response.notification.NotificationResponseDTO;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.NotificationRepository;
import com.example.courier.service.person.PersonService;
import com.example.courier.util.AuthUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final PersonService personService;


    public NotificationService(NotificationRepository notificationRepository, PersonService personService) {
        this.notificationRepository = notificationRepository;
        this.personService = personService;
    }

    public void broadcastToType(Class<? extends Person> personClass, NotificationMessage message) {
        List<? extends Person> recipients = personService.getAllActiveByType(personClass);
        createNotificationWithRecipients(message, recipients);
    }

    private void createNotificationWithRecipients(NotificationMessage message, Collection<? extends Person> recipients) {
        Notification notification = new Notification(
                message.title(),
                message.message(),
                LocalDateTime.now()
        );
        notification.setRecipients(new HashSet<>(recipients));
        notificationRepository.save(notification);
    }

    public PaginatedResponseDTO<NotificationResponseDTO> getNotificationsPaginated(int size, int page) {
        Long recipientId = AuthUtils.getAuthenticatedPersonId();

        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notificationPage = notificationRepository.findAllByRecipientIdPageable(recipientId, pageable);

        List<NotificationResponseDTO> content = notificationPage.getContent()
                .stream()
                .map(this::convertToDto)
                .toList();

        return new PaginatedResponseDTO<>(
                content,
                page,
                notificationPage.getTotalElements(),
                notificationPage.getTotalPages()
        );
    }

    public NotificationResponseDTO convertToDto(Notification notification) {
        return new NotificationResponseDTO(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getCreatedAt(),
                notification.isRead()
        );
    }









    public void notifySinglePerson(Long personId, NotificationMessage message) {
        Person person = personService.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with ID: " + personId));

      //  Notification notification = new Notification(message.title(), message.message(), person, LocalDateTime.now());
       // notificationRepository.save(notification);
    }


    public void notifyAdminCourierCheckIn(Long taskId, Long courierId) {
        String title = String.format("Courier ID %d checked in", courierId);
        String message = String.format("Courier ID = %d has checked in for Task ID = %d", courierId, taskId);

        List<Admin> adminList = personService.fetchAllByType(Admin.class);

        //Notification notification = new Notification(title, message, LocalDateTime.now());

        //notificationRepository.save(notification);
    }




}
