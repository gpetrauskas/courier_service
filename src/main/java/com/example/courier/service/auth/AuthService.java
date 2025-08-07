package com.example.courier.service.auth;

import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.LoginDTO;
import com.example.courier.dto.jwt.JwtClaims;
import com.example.courier.exception.UserNotFoundException;
import com.example.courier.repository.PersonRepository;
import com.example.courier.repository.UserRepository;
import com.example.courier.service.person.PersonService;
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
    private PersonService personService;
    @Autowired
    private JwtService jwtService;

    @Transactional
    public ApiResponseDTO loginUser(LoginDTO loginDTO, HttpServletResponse response) {
        try {
            Person person = personRepository.findByEmail(loginDTO.email()).orElseThrow(() ->
                    new RuntimeException("Invalid credentials"));

            if (!passwordEncoder.matches(loginDTO.password(), person.getPassword())) {
                throw new BadCredentialsException("Invalid credentials");
            }

                String jwt = jwtService.createToken(loginDTO.email(), person.getRole(), person.getName());
                JwtClaims tokenDetails = jwtService.validateToken(jwt);
                String encryptedAuthToken = jwtService.encryptAuthToken(tokenDetails.authToken());

                setCookies(response, jwt, encryptedAuthToken);

                return new ApiResponseDTO("success", "Logged in successfully.");
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
}