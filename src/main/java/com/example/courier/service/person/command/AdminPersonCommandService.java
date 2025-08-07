package com.example.courier.service.person.commands;

import com.example.courier.domain.Person;
import com.example.courier.dto.request.PersonDetailsUpdateRequest;
import com.example.courier.repository.PersonRepository;
import com.example.courier.service.transformation.PersonTransformationService;
import com.example.courier.service.validation.PersonValidationService;
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
    private final PersonRepository repository;
    private final PersonDetailsValidator validator;
    private final PersonValidationService personValidationService;
    private final PersonTransformationService transformationService;


    public AdminPersonCommandService(PersonDetailsValidator validator, PersonValidationService personValidationService,
                                     PersonTransformationService transformationService, PersonRepository repository) {
        this.validator = validator;
        this.personValidationService = personValidationService;
        this.transformationService = transformationService;
        this.repository = repository;
    }

    @Transactional
    public void updateDetails(PersonDetailsUpdateRequest updateRequest, Person person) {
        validator.validate(updateRequest);

        FieldUpdater.updateIfValid(updateRequest.name(), personValidationService::isNameValid, person::setName);
        FieldUpdater.updateIfValid(updateRequest.email(), personValidationService::isEmailValid, person::setEmail);
        FieldUpdater.updateAndTransformIfValid(updateRequest.phoneNumber(), personValidationService::isPhoneValid,
                transformationService::formatPhone, person::setPhoneNumber);

        repository.save(person);

        logger.info("Updated person with ID {}", person.getId());
    }

    @Transactional
    public void softDelete(Person person) {
        person.delete();

        repository.save(person);
        logger.info("Person with ID {}, deleted successfully", person.getId());
    }
}
