package com.example.courier.service.person.account;

import com.example.courier.domain.Person;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.person.PasswordChangeDTO;
import com.example.courier.dto.response.person.PersonResponseDTO;
import com.example.courier.repository.PersonRepository;
import com.example.courier.service.person.strategy.PersonInfoStrategyResolver;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.service.validation.PersonValidationService;
import jakarta.validation.ValidationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service for handling account related operations for currently authenticated person.
 * <p> Provides functionality to:
 * <ul>
 *     <li>Retrieve the current person profile information.</li>
 *     <li>Change current persons password.</li>
 * </ul>
 *
 * This service relies on {@link CurrentPersonService} to identify the authenticated person
 * and delegate profile mapping to {@link PersonInfoStrategyResolver}
 */
@Service
public class PersonAccountService {
    private final PersonRepository personRepository;
    private final CurrentPersonService currentPersonService;
    private final PersonInfoStrategyResolver personInfoStrategyResolver;
    private final PasswordEncoder passwordEncoder;
    private final PersonValidationService validationService;

    public PersonAccountService(CurrentPersonService currentPersonService, PersonInfoStrategyResolver personInfoStrategyResolver,
                                PasswordEncoder passwordEncoder, PersonValidationService validationService,
                                PersonRepository personRepository) {
        this.currentPersonService = currentPersonService;
        this.personInfoStrategyResolver = personInfoStrategyResolver;
        this.passwordEncoder = passwordEncoder;
        this.validationService = validationService;
        this.personRepository = personRepository;
    }

    /** Retrieves the profile information of the current person.
     *
     * Returned {@link PersonResponseDTO} will be one of permitted type:
     * {@code UserResponseDTO}, {@code AdminPersonResponseDTO}, {@code AdminProfileResponseDTO}
     * depending on role of current person.
     *
     * @return a role specific {@link PersonResponseDTO} containing person info
     */
    public PersonResponseDTO myInfo() {
        Person person = fetchCurrentPerson();
        return personInfoStrategyResolver.resolve(person);
    }

    /** Changes the password for current user.
     *
     * @param dto the password change request containing current and new passwords
     * @return an {@link ApiResponseDTO} response message
     * @throws ValidationException if validation fails
     * */
    @Transactional
    public ApiResponseDTO changePassword(PasswordChangeDTO dto) {
        if (dto == null || dto.newPassword() == null || dto.currentPassword() == null) {
            throw new ValidationException("Password fields cannot be null");
        }

        Person person = fetchCurrentPerson();
        if (!passwordEncoder.matches(dto.currentPassword(), person.getPassword())) {
            throw new ValidationException("Current password do not match.");
        }

        validationService.validatePassword(dto.newPassword());

        person.setPassword(passwordEncoder.encode(dto.newPassword()));
        personRepository.save(person);

        return new ApiResponseDTO("success", "Password updated successfully.");
    }

    private Person fetchCurrentPerson() {
        return currentPersonService.getCurrentPerson();
    }
}
