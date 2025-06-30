package com.example.courier.service.notification.strategy;

import com.example.courier.domain.Notification;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.notification.NotificationRequestDTO;
import com.example.courier.repository.NotificationRepository;
import com.example.courier.repository.PersonNotificationRepository;
import com.example.courier.service.notification.NotificationTarget;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IndividualNotificationStrategy implements NotificationDeliveryStrategy {

    private final PersonNotificationRepository personNotificationRepository;
    private final NotificationRepository notificationRepository;

    public IndividualNotificationStrategy(
            PersonNotificationRepository personNotificationRepository, NotificationRepository notificationRepository
    ) {
        this.personNotificationRepository = personNotificationRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public boolean supports(NotificationTarget target) {
        return target instanceof NotificationTarget.Individual;
    }

    @Override
    public ApiResponseDTO deliver(NotificationRequestDTO requestDTO) {
        NotificationTarget.Individual individual = (NotificationTarget.Individual) requestDTO.type();
        Long personId = Optional.ofNullable(individual.personId()).orElseThrow(() ->
                new IllegalArgumentException("Person ID cannot be null"));

        Notification notification = new Notification(requestDTO.title(), requestDTO.message(), LocalDateTime.now());
        notificationRepository.save(notification);
        personNotificationRepository.bulkInsert(notification.getId(), List.of(personId));
        return new ApiResponseDTO("success", "Notification send to person with ID: " + personId);
    }
}
