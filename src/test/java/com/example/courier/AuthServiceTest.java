package com.example.courier;

import com.example.courier.domain.Person;
import com.example.courier.dto.LoginDTO;
import com.example.courier.dto.jwt.JwtClaims;
import com.example.courier.repository.PersonRepository;
import com.example.courier.repository.UserRepository;
import com.example.courier.service.auth.AuthService;
import com.example.courier.service.auth.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private HttpServletResponse response;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private AuthService authService;

    @Test
    @Transactional
    void login_ShouldReturnSuccess_WhenCredentialsValid() {

        LoginDTO loginDTO = new LoginDTO("test@example.com", "password");
        Person person = new Person() {
            @Override
            public String getRole() {
                return "USER";
            }
        };
        person.setPassword("encodedPassword");
        person.setName("Name Surname");

        JwtClaims mockClaims = new JwtClaims(
                loginDTO.email(),
                person.getRole(),
                person.getName(),
                "mockAuthToken"
        );

        when(personRepository.findByEmail(loginDTO.email())).thenReturn(Optional.of(person));
        when(passwordEncoder.matches(loginDTO.password(), person.getPassword())).thenReturn(true);
        when(jwtService.createToken(anyString(), anyString(), anyString())).thenReturn("fakeJWT");
        when(jwtService.validateToken("fakeJWT")).thenReturn(mockClaims);
        when(jwtService.encryptAuthToken(anyString())).thenReturn("encryptedToken");

        Map<String, String> results = authService.loginUser(loginDTO, response);

        assertEquals("Login successfully.", results.get("message"));
        verify(response, times(2)).addCookie(any());
        verify(response).addCookie(argThat(cookie -> "jwt".equals(cookie.getName())));
        verify(response).addCookie(argThat(cookie -> "authToken".equals(cookie.getName())));
    }

    @Test
    void login_ShouldThrow_WhenPasswordInvalid() {
        LoginDTO loginDTO = new LoginDTO("example@example.com", "wrongPassword");
        Person person = new Person() {
            @Override
            public String getRole() {
                return "";
            }
        };
        person.setPassword("goodPassword");

        when(personRepository.findByEmail(loginDTO.email())).thenReturn(Optional.of(person));
        when(passwordEncoder.matches(loginDTO.password(), person.getPassword())).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> authService.loginUser(loginDTO, response));
    }

    @Test
    void login_ShouldThrow_WhenUserNotFound() {
        LoginDTO loginDTO = new LoginDTO("test@example.com", "password");
        when(personRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.loginUser(loginDTO, response));
    }
}