package com.example.courier.service;

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
            logger.info("before validation");
            validateUserRegistration(userDTO);
            logger.info("before creating user");
            User newUser = createUserFromDTO(userDTO);
            logger.info("before saving user");
            userRepository.save(newUser);
        } catch (RuntimeException e) {
            System.out.println("Error during registration: User already registered with email: " + userDTO.email() );
            throw e;
        }
    }

    private void validateUserRegistration(UserDTO userDTO) {
        User user = userRepository.findByEmail(userDTO.email());
        if (userDTO.password().length() < 8 || userDTO.password().length() > 16) {
            logger.info("Password length");
            throw new ValidationException("Password length must be between 8-16 characters.");
        } else if (user != null) {
            logger.info("User not null");
            throw new RuntimeException("User already exist.");
        }
    }

    public User createUserFromDTO(UserDTO userDTO) throws RuntimeException {
        User newUser = new User();
        newUser.setName(userDTO.name());
        newUser.setEmail(userDTO.email());
        newUser.setAddress(userDTO.address());
        String encodedPass = passwordEncoder.encode(userDTO.password());
        newUser.setPassword(encodedPass);
        logger.info("all good");
        return newUser;
    }

    public String login(LoginDTO loginDTO) {
        try {
            User user = userRepository.findByEmail(loginDTO.email());
            if (user != null) {
                if (passwordEncoder.matches(loginDTO.password(), user.getPassword())) {
                    return jwtService.createToken(loginDTO.email());
                } else {
                    throw new RuntimeException("Password do not match");
                }
            } else {
                throw new RuntimeException("Email not found");
            }
        } catch (Exception e) {
            throw e;
        }
    }



}
