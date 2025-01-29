package com.example.courier.service;

import com.example.courier.domain.Courier;
import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import com.example.courier.dto.RegistrationDTO;
import com.example.courier.repository.CourierRepository;
import com.example.courier.repository.PersonRepository;
import com.example.courier.repository.UserRepository;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {
    private final Logger logger = LoggerFactory.getLogger(RegistrationService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CourierRepository courierRepository;


    @Transactional
    public void registerUser(RegistrationDTO registrationDTO) {
        try {
            logger.info("Trying to register user");
            validateUserRegistration(registrationDTO);
            User newUser = createPersonFromDTO(registrationDTO, User.class);
            userRepository.save(newUser);
            logger.info("User registration successful");
        } catch (ValidationException e) {
            logger.error("Validation error during registration {}", e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            logger.error("Unexpected error during registration {}", e.getMessage());
            throw new RuntimeException("Failed to register user", e);
        }
    }

    private void validateUserRegistration(RegistrationDTO registrationDTO) {
        logger.info("Checking if user exists with email: {}", registrationDTO.email());

        if (personRepository.existsByEmail(registrationDTO.email())) {
            logger.warn("Registration failed: EMail {} already registered", registrationDTO.email());
            throw new ValidationException("Email " + registrationDTO.email() + " is already registered");
        }

        logger.info("Checking password length");
        if (registrationDTO.password().length() < 8 || registrationDTO.password().length() > 16) {
            logger.warn("Password is to short or to long");
            throw new ValidationException("Password length must be between 8-16 characters");
        }

        logger.info("Validating email format");
        if (!registrationDTO.email().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            logger.warn("Registration failed. Email format is invalid");
            throw new ValidationException("Email is not valid.");
        }
    }

    private <T extends Person> T createPersonFromDTO(RegistrationDTO registrationDTO, Class<T> personClass) throws RuntimeException {
        try {
            T person = personClass.getDeclaredConstructor().newInstance();
            person.setName(registrationDTO.name());
            person.setEmail(registrationDTO.email());
            person.setPassword(passwordEncoder.encode(registrationDTO.password()));
            return person;
        } catch (Exception e) {
            logger.error("Failed to create person from dto: {}", e.getMessage());
            throw new RuntimeException("Failed to create person from dto.");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void createCourier(RegistrationDTO registrationDTO) {
        try {
            logger.info("Courier register request");
            validateUserRegistration(registrationDTO);
            Courier courier = createPersonFromDTO(registrationDTO, Courier.class);
            courierRepository.save(courier);
            logger.info("Courier registration successful");
        } catch (ValidationException e) {
            logger.error("Validation error during registration {}", e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            logger.error("Unexpected error during registration {}", e.getMessage());
            throw new RuntimeException("Failed to register courier");
        }
    }
}
