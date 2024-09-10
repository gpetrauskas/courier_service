package com.example.courier.service;

import com.example.courier.common.Role;
import com.example.courier.domain.User;
import com.example.courier.dto.LoginDTO;
import com.example.courier.dto.UserDTO;
import com.example.courier.exception.UserNotFoundException;
import com.example.courier.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

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
            logger.error("Validation error during registration: {}", e.getMessage());
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
        String encodedPass = passwordEncoder.encode(userDTO.password());
        newUser.setPassword(encodedPass);
        newUser.setRole(Role.USER);
        return newUser;
    }

    @Transactional
    public Map<String, String> loginUser(LoginDTO loginDTO, HttpServletResponse response) {
        try {
            User user = userRepository.findByEmail(loginDTO.email());
            if (user != null && passwordEncoder.matches(loginDTO.password(), user.getPassword())) {
                String jwt = jwtService.createToken(loginDTO.email(), user.getRole().toString(), user.getName());
                Map<String, String> tokenDetails = jwtService.validateToken(jwt);
                String authToken = tokenDetails.get("authToken");
                String encryptedAuthToken = jwtService.encryptAuthToken(authToken);

                setCookies(response, jwt, encryptedAuthToken);

                Map<String, String> responseMap = new HashMap<>();
                responseMap.put("message", "Login successfully");

                return responseMap;
            }
        } catch (Exception e) {
            logger.error("Error occurred during login", e);
        }
        throw new RuntimeException("Invalid credentials.");
    }

    private void setCookies(HttpServletResponse response, String jwtToken, String encryptedAuthToken) {
        setJwtCookie(response, jwtToken);
        setAuthCookie(response, encryptedAuthToken);
    }

    private void setJwtCookie(HttpServletResponse response, String jwtToken) {
        Cookie jwtCookie = new Cookie("jwt", jwtToken);
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(false);
        jwtCookie.setAttribute("SameSite", "Strict");
        response.addCookie(jwtCookie);
    }

    private void setAuthCookie(HttpServletResponse response, String encryptedAuthToken) {
        Cookie authCookie = new Cookie("authToken", encryptedAuthToken);
        authCookie.setPath("/");
        authCookie.setHttpOnly(false);
        authCookie.setAttribute("SameSite", "Strict");
        response.addCookie(authCookie);
    }

    /*
    public void logoutUser(HttpServletResponse response) {
        try {
            invalidateCookie(response, "jwt", true);
            invalidateCookie(response, "authToken", false);
            Map<String, String> logoutResponse = new HashMap<>();
            logoutResponse.put("message", "Logout successful");
        } catch (Exception e) {
            throw new RuntimeException("Logout failed", e);
        }
    }

    private void invalidateCookie(HttpServletResponse response, String cookieName, boolean httpOnly) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(httpOnly);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

     */

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email).getId();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}