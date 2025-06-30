package com.example.courier.service.notification.strategy;

import com.example.courier.domain.*;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.notification.NotificationRequestDTO;
import com.example.courier.repository.NotificationRepository;
import com.example.courier.repository.PersonNotificationRepository;
import com.example.courier.service.notification.NotificationTarget;
import com.example.courier.service.person.PersonService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BroadcastNotificationStrategy implements NotificationDeliveryStrategy {

    private final PersonService personService;
    private final NotificationRepository notificationRepository;
    private final PersonNotificationRepository personNotificationRepository;

    public BroadcastNotificationStrategy(PersonService personService, NotificationRepository notificationRepository,
                                         PersonNotificationRepository personNotificationRepository) {
        this.personService = personService;
        this.notificationRepository = notificationRepository;
        this.personNotificationRepository = personNotificationRepository;
    }

    @Override
    public boolean supports(NotificationTarget target) {
        return target instanceof NotificationTarget.BroadCast;
    }

    @Override
    public ApiResponseDTO deliver(NotificationRequestDTO requestDTO) {
        NotificationTarget.BroadCast broadCast = (NotificationTarget.BroadCast) requestDTO.type();
        Class<? extends Person> personClass = switch (broadCast.type()) {
            case USER -> User.class;
            case COURIER -> Courier.class;
            case ADMIN -> Admin.class;
        };

        List<Long> recipients = personService.findAllActiveIdsByType(personClass);
        if (recipients.isEmpty()) {
            return new ApiResponseDTO("warning", "No recipients was found for " + personClass.getSimpleName());
        }

        Notification notification = new Notification(requestDTO.title(), requestDTO.message(), LocalDateTime.now());
        notificationRepository.save(notification);
        personNotificationRepository.bulkInsert(notification.getId(), recipients);

        return new ApiResponseDTO("success", "Notification sent to " + recipients.size() + " " + personClass.getSimpleName() + "(s)");
    }
}
