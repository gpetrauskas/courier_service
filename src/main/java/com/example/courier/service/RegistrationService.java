package com.example.courier.service;

import com.example.courier.common.ApiResponseType;
import com.example.courier.domain.Courier;
import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.RegistrationDTO;
import com.example.courier.repository.PersonRepository;
import com.example.courier.service.person.PersonService;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.validation.PasswordValidator;
import com.example.courier.validation.RegistrationValidator;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {
    private final Logger logger = LoggerFactory.getLogger(RegistrationService.class);
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final RegistrationValidator registrationValidator;
    private final PersonService personService;
    private final PasswordValidator passwordValidator;
    private final CurrentPersonService currentPersonService;

    RegistrationService(PersonRepository personRepository,
                        PasswordEncoder passwordEncoder, RegistrationValidator registrationValidator,
                        PersonService personService, PasswordValidator passwordValidator,
                        CurrentPersonService currentPersonService
    ) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
        this.registrationValidator = registrationValidator;
        this.personService = personService;
        this.passwordValidator = passwordValidator;
        this.currentPersonService= currentPersonService;
    }

    @Transactional
    public ApiResponseDTO registerUser(RegistrationDTO registrationDTO) {
            logger.info("Trying to register user with email {}", registrationDTO.email());
            validateAndRegister(registrationDTO, new User(registrationDTO.name(), registrationDTO.email(), passwordEncoder.encode(registrationDTO.password())));
            return ApiResponseType.USER_REGISTRATION_SUCCESS.apiResponseDTO();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ApiResponseDTO registerCourier(RegistrationDTO registrationDTO) {
        logger.info("Trying to register courier");
        validateAndRegister(registrationDTO, new Courier(registrationDTO.name(), registrationDTO.email(), passwordEncoder.encode(registrationDTO.password())));
        return ApiResponseType.COURIER_REGISTRATION_SUCCESS.withParams(currentPersonService.getCurrentPerson().getEmail());
    }

    private <T extends Person> void validateAndRegister(RegistrationDTO registrationDTO, T entity) {
        // fast check if user already registered
        checkIfUserAlreadyExists(registrationDTO.email());
        // validation
        registrationValidator.validateUserRegistration(registrationDTO);
        passwordValidator.validatePassword(registrationDTO.password());

        personRepository.save(entity);
        logger.info("{} registered successfully: {}", entity.getClass().getSimpleName(), registrationDTO.email());
    }

    private void checkIfUserAlreadyExists(String email) {
        logger.info("Checking if user exists with email: {}", email);
        if (personService.checkIfPersonAlreadyExistsByEmail(email)) {
            logger.warn("Registration failed: Email {} already registered", email);
            throw new ValidationException("Email " + email + " is already registered");
        }
    }
}
