package com.example.courier.service;

import com.example.courier.domain.Admin;
import com.example.courier.domain.Courier;
import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import com.example.courier.dto.LoginDTO;
import com.example.courier.dto.jwt.JwtClaims;
import com.example.courier.exception.UserNotFoundException;
import com.example.courier.repository.PersonRepository;
import com.example.courier.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

            if (!passwordEncoder.matches(loginDTO.password(), person.getPassword())) {
                throw new BadCredentialsException("Invalid credentials");
            }

                String jwt = jwtService.createToken(loginDTO.email(), person.getRole(), person.getName());
                JwtClaims tokenDetails = jwtService.validateToken(jwt);
                String encryptedAuthToken = jwtService.encryptAuthToken(tokenDetails.authToken());

                setCookies(response, jwt, encryptedAuthToken);

                return Map.of("message", "Login successfully.");
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            logger.warn("Login failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during login.", e);
            throw new RuntimeException("Unexpected error occurred during login");
        }
    }

    private void setCookies(HttpServletResponse response, String jwtToken, String encryptedAuthToken) {
        setCookie(response, jwtToken, "jwt");
        setCookie(response, encryptedAuthToken, "authToken");
    }

    private void setCookie(HttpServletResponse response, String token, String name) {
        Cookie cookie = new Cookie(name, token);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
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

    public Person findByUsername(String username) {
        return personRepository.findByEmail(username).orElseThrow(() ->
             new UserNotFoundException("Person not found with username/email: " + username));
    }
}