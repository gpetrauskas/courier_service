package com.example.courier.service.notification.strategy;

import com.example.courier.domain.*;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.notification.NotificationRequestDTO;
import com.example.courier.service.notification.NotificationFactory;
import com.example.courier.service.notification.NotificationTarget;
import com.example.courier.service.person.PersonService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BroadcastNotificationStrategy implements NotificationDeliveryStrategy {

    private final PersonService personService;
    private final NotificationFactory factory;

    public BroadcastNotificationStrategy(PersonService personService, NotificationFactory factory) {
        this.personService = personService;
        this.factory = factory;
    }

    @Override
    public Class<? extends NotificationTarget> getSupportedType() {
        return NotificationTarget.BroadCast.class;
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

        return factory.createNotification(requestDTO.title(), requestDTO.message(), recipients);
    }
}
