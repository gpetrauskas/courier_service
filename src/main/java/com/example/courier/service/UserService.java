package com.example.courier.service;

import com.example.courier.common.Role;
import com.example.courier.domain.User;
import com.example.courier.dto.LoginDTO;
import com.example.courier.dto.UserDTO;
import com.example.courier.repository.UserRepository;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    @Transactional
    public void registerUser(UserDTO userDTO) {
        try {
            logger.info("Validating user registration");
            validateUserRegistration(userDTO);
            logger.info("Creating user from DTO");
            User newUser = createUserFromDTO(userDTO);
            logger.info("Saving user to repository");
            userRepository.save(newUser);
            logger.info("User registration successful");
        } catch (ValidationException e) {
            logger.info("Validation error during registration: {}", e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            logger.error("Unexpected error during registration: {}", e.getMessage());
            throw e;
        }
    }

    private void validateUserRegistration(UserDTO userDTO) {
        logger.debug("Checking if user exists");
        User user = userRepository.findByEmail(userDTO.email());

        if (user != null) {
            logger.warn("User already exists");
            throw new RuntimeException("User already exists");
        }
        logger.debug("Validating password length");
        if (userDTO.password().length() < 8 || userDTO.password().length() > 16) {
            logger.info("Password length");
            throw new ValidationException("Password length must be between 8-16 characters.");
        }
        logger.debug("Validating email format");
        if (!userDTO.email().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Email is not valid.");
        }
    }

    public User createUserFromDTO(UserDTO userDTO) throws RuntimeException {
        User newUser = new User();
        newUser.setName(userDTO.name());
        newUser.setEmail(userDTO.email());
        newUser.setAddress(userDTO.address());
        String encodedPass = passwordEncoder.encode(userDTO.password());
        newUser.setPassword(encodedPass);
        newUser.setRole(Role.USER);
        return newUser;
    }

    public String loginUser(LoginDTO loginDTO) {
        try {
            User user = userRepository.findByEmail(loginDTO.email());
            if (user != null && passwordEncoder.matches(loginDTO.password(), user.getPassword())) {
                return jwtService.createToken(loginDTO.email(), user.getRole().toString());
            }
        } catch (Exception e) {
            logger.error("Error occurred during login", e);
        }
        throw new RuntimeException("Invalid credentials.");
    }
}