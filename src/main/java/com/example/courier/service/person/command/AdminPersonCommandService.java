package com.example.courier.service.person.command;

import com.example.courier.domain.Person;
import com.example.courier.dto.request.PersonDetailsUpdateRequest;
import com.example.courier.service.person.query.PersonLookupService;
import com.example.courier.service.transformation.PersonTransformationService;
import com.example.courier.validation.FieldUpdater;
import com.example.courier.validation.person.PersonDetailsValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@PreAuthorize("hasRole('ADMIN')")
public class AdminPersonCommandService {
    private final Logger logger = LoggerFactory.getLogger(AdminPersonCommandService.class);
    private final PersonUpdateService updateService;
    private final PersonDetailsValidator detailsValidator;
    private final PersonTransformationService transformationService;
    private final PersonLookupService lookupService;


    public AdminPersonCommandService(
            PersonDetailsValidator detailsValidator,
            PersonTransformationService transformationService,
            PersonUpdateService updateSrvice,
            PersonLookupService lookupService) {
        this.detailsValidator = detailsValidator;
        this.transformationService = transformationService;
        this.updateService = updateSrvice;
        this.lookupService = lookupService;
    }

    @Transactional
    public void updateDetails(Long personId, PersonDetailsUpdateRequest updateRequest) {
        detailsValidator.validate(updateRequest);
        checkEmailUniqueness(updateRequest.email());

        Person person = lookupService.fetchById(personId);

        updateIfPresent(person, updateRequest);

        updateService.persist(person);

        logger.info("Updated person with ID {}", person.getId());
    }

    @Transactional
    public void softDelete(Long personId) {
        Person person = lookupService.fetchById(personId);
        person.setDeleted(true);

        updateService.persist(person);
        logger.info("Person with ID {}, deleted successfully", person.getId());
    }

    private void updateIfPresent(Person person, PersonDetailsUpdateRequest updateRequest) {
        FieldUpdater.updateIfPresent(updateRequest.name(), person::setName);
        FieldUpdater.updateIfPresent(updateRequest.email(), person::setEmail);
        FieldUpdater.updateAndTransformIfPresent(updateRequest.phoneNumber(),
                transformationService::formatPhone, person::setPhoneNumber);
    }

    private void checkEmailUniqueness(String email) {
        if (email != null && !email.isBlank() && lookupService.checkIfPersonAlreadyExistsByEmail(email)) {
            throw new IllegalArgumentException("Email already used");
        }
    }
}
