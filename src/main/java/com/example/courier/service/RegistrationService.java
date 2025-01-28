package com.example.courier.service;

import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import com.example.courier.dto.RegistrationDTO;
import com.example.courier.repository.PersonRepository;
import com.example.courier.repository.UserRepository;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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


    @Transactional
    public void registerUser(RegistrationDTO registrationDTO) {
        try {
            logger.info("Trying to register user");
            validateUserRegistration(registrationDTO);
            logger.info("Creating user from dto");
            User newUser = createUserFromDTO(registrationDTO);
            logger.info("Saving user");
            userRepository.save(newUser);
            logger.info("User registration successful");
        } catch (ValidationException e) {
            logger.error("Validation error during registration {}", e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            logger.error("Unexpected error during registration {}", e.getMessage());
            throw e;
        }
    }

    private void validateUserRegistration(RegistrationDTO registrationDTO) {
        logger.info("Checking if user exists with email: {}", registrationDTO.email());
        boolean emailAlreadyExists = personRepository.existsByEmail(registrationDTO.email());
        if (emailAlreadyExists) {
            logger.warn("Registration failed: EMail {} already registered", registrationDTO.email());
            throw new ValidationException("Email " + registrationDTO.email() + " is already registered");
        }

        logger.info("Checking password length");
        if (registrationDTO.password().length() < 8 || registrationDTO.password().length() > 16) {
            logger.info("Password is to short or to long");
            throw new ValidationException("Password length must be between 8-16 characters");
        }

        logger.info("Validating email format");
        if (!registrationDTO.email().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Email is not valid.");
        }
    }

    private User createUserFromDTO(RegistrationDTO registrationDTO) throws RuntimeException {
        User newUser = new User();
        newUser.setName(registrationDTO.name());
        newUser.setEmail(registrationDTO.email());
        String encodedPass = passwordEncoder.encode(registrationDTO.password());
        newUser.setPassword(encodedPass);

        return newUser;
    }

}
