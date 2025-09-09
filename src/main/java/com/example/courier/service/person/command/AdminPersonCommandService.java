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

/** Service providing administrative commands for managing {@link Person} entities.
 *
 * Accessible only to users with {@code ADMIN} role.
 * Supports updating person details and performing soft deletions.
 */
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
            PersonUpdateService updateService,
            PersonLookupService lookupService) {
        this.detailsValidator = detailsValidator;
        this.transformationService = transformationService;
        this.updateService = updateService;
        this.lookupService = lookupService;
    }

    /** Updates the details of the specified {@link Person}.
     *
     * Validates the provided update request, ensures email is unique (if given),
     * fetches the target person and applies any nonnull fields from request.
     * Phone numbers are formatted via {@link PersonTransformationService} before being set.
     *
     * @param personId the ID of the person to update
     * @param updateRequest the {@link PersonDetailsUpdateRequest} containing updated details
     * @throws IllegalArgumentException if email is already used.
     */
    @Transactional
    public void updateDetails(Long personId, PersonDetailsUpdateRequest updateRequest) {
        detailsValidator.validate(updateRequest);
        checkEmailUniqueness(updateRequest.email());

        Person person = lookupService.fetchById(personId);

        updateIfPresent(person, updateRequest);

        updateService.persist(person);

        logger.info("Updated person with ID {}", person.getId());
    }

    /** Soft delete a specified {@link Person}.
     *
     * Fetches person and sets isDeleted field to {@code true}, without actually removal from DB.
     *
     * @param personId the id of the person to delete.
     * */
    @Transactional
    public void softDelete(Long personId) {
        Person person = lookupService.fetchById(personId);
        person.setDeleted(true);

        updateService.persist(person);
        logger.info("Person with ID {}, deleted successfully", person.getId());
    }

    /* Helper methods
    */
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
