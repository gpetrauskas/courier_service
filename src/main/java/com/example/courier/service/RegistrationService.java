package com.example.courier.service;

import com.example.courier.domain.Courier;
import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import com.example.courier.dto.RegistrationDTO;
import com.example.courier.repository.CourierRepository;
import com.example.courier.repository.PersonRepository;
import com.example.courier.repository.UserRepository;
import com.example.courier.service.person.PersonService;
import com.example.courier.validation.RegistrationValidator;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class RegistrationService {
    private final Logger logger = LoggerFactory.getLogger(RegistrationService.class);
    private final UserRepository userRepository;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final CourierRepository courierRepository;
    private final RegistrationValidator registrationValidator;
    private final PersonService personService;

    RegistrationService(UserRepository userRepository, PersonRepository personRepository,
                        PasswordEncoder passwordEncoder, CourierRepository courierRepository,
                        RegistrationValidator registrationValidator, PersonService personService) {
        this.userRepository = userRepository;
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
        this.courierRepository = courierRepository;
        this.registrationValidator = registrationValidator;
        this.personService = personService;
    }


    @Transactional
    public void registerUser(RegistrationDTO registrationDTO) {
            logger.info("Trying to register user with email {}", registrationDTO.email());

            validateAndRegister(
                    registrationDTO,
                    () -> new User(
                            registrationDTO.name(),
                            registrationDTO.email(),
                            passwordEncoder.encode(registrationDTO.password())
                    ),
                    personRepository::save
            );

            logger.info("User registered successfully: {}", registrationDTO.email());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void registerCourier(RegistrationDTO registrationDTO) {
        logger.info("Trying to register courier");

        validateAndRegister(
                registrationDTO,
                () -> new Courier(
                        registrationDTO.name(),
                        registrationDTO.email(),
                        passwordEncoder.encode(registrationDTO.password())),
                personRepository::save
        );

        logger.info("Courier registered successfully {}", registrationDTO.email());
    }

    private void checkIfUserAlreadyExists(String email) {
        logger.info("Checking if user exists with email: {}", email);

        if (personService.checkIfPersonAlreadyExistsByEmail(email)) {
            logger.warn("Registration failed: EMail {} already registered", email);
            throw new ValidationException("Email " + email + " is already registered");
        }
    }

    private <T extends Person> void validateAndRegister(
            RegistrationDTO registrationDTO,
            Supplier<T> entityCreator,
            Consumer<T> entitySaver
    ) {
        registrationValidator.validateUserRegistration(registrationDTO);
        checkIfUserAlreadyExists(registrationDTO.email());

        T entity = entityCreator.get();
        entitySaver.accept(entity);
    }
}
