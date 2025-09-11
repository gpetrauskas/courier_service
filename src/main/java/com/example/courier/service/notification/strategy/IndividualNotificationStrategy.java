package com.example.courier.service.notification.strategy;

import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.notification.NotificationRequestDTO;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.service.notification.NotificationFactory;
import com.example.courier.service.notification.NotificationTarget;
import com.example.courier.service.person.query.PersonLookupService;
import org.springframework.stereotype.Service;

import java.util.List;

/** {@link NotificationDeliveryStrategy} for sending notifications to a single person.
 *
 * <p>
 *     Validates that the target person exists and is active before sending.
 * </p>
 *
 * */
@Service
public class IndividualNotificationStrategy implements NotificationDeliveryStrategy {

    private final NotificationFactory factory;
    private final PersonLookupService personLookupService;

    public IndividualNotificationStrategy(NotificationFactory factory, PersonLookupService personLookupService) {
        this.factory = factory;
        this.personLookupService = personLookupService;
    }

    @Override
    public Class<? extends NotificationTarget> getSupportedType() {
        return NotificationTarget.Individual.class;
    }

    @Override
    public ApiResponseDTO deliver(NotificationRequestDTO requestDTO) {
        NotificationTarget.Individual individual = (NotificationTarget.Individual) requestDTO.type();
        if (!personLookupService.existsByIdAndIsActive(individual.personId())) {
            throw new ResourceNotFoundException("User with ID " + individual.personId() + " was not found");
        }

        return factory.createNotification(requestDTO.title(), requestDTO.message(), List.of(individual.personId()));
    }
}
