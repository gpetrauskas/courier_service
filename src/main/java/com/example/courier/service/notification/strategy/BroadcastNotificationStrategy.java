package com.example.courier.service.notification.strategy;

import com.example.courier.domain.*;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.notification.NotificationRequestDTO;
import com.example.courier.service.notification.NotificationFactory;
import com.example.courier.service.notification.NotificationTarget;
import com.example.courier.service.person.PersonFacade;
import com.example.courier.service.person.query.PersonLookupService;
import org.springframework.stereotype.Service;

import java.util.List;

/** {@link NotificationDeliveryStrategy} for broadcasting notifications to all active persons of a given class.
 *
 * <p>
 *     Supports broadcasting to {@link User}, {@link Courier} or {@link Admin} recipients.
 * </p>
 */
@Service
public class BroadcastNotificationStrategy implements NotificationDeliveryStrategy {

    private final PersonLookupService lookupService;
    private final NotificationFactory factory;

    public BroadcastNotificationStrategy(PersonLookupService lookupService, NotificationFactory factory) {
        this.lookupService = lookupService;
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

        List<Long> recipients = lookupService.findAllActiveIdsByType(personClass);
        if (recipients.isEmpty()) {
            return new ApiResponseDTO("warning", "No recipients was found for " + personClass.getSimpleName());
        }

        return factory.createNotification(requestDTO.title(), requestDTO.message(), recipients);
    }
}
