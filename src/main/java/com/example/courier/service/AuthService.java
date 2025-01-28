package com.example.courier.service;

import com.example.courier.domain.Admin;
import com.example.courier.domain.Courier;
import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import com.example.courier.dto.LoginDTO;
import com.example.courier.exception.UserNotFoundException;
import com.example.courier.repository.PersonRepository;
import com.example.courier.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private Logger logger = LoggerFactory.getLogger(AuthService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    @Transactional
    public Map<String, String> loginUser(LoginDTO loginDTO, HttpServletResponse response) {
        try {
            var person = personRepository.findByEmail(loginDTO.email()).orElseThrow(() ->
                    new RuntimeException("Invalid credentials"));

            if (passwordEncoder.matches(loginDTO.password(), person.getPassword())) {
                String role = getRole(person);

                String jwt = jwtService.createToken(loginDTO.email(), role, person.getName());
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

    private String getRole(Person person) {
        return switch (person) {
            case Admin ignored -> "ADMIN";
            case Courier ignored -> "COURIER";
            case User ignored -> "USER";
                default -> throw new IllegalArgumentException("Unknown person type: " + person.getClass());
        };
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