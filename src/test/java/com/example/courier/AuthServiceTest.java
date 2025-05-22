package com.example.courier;

import com.example.courier.domain.Person;
import com.example.courier.dto.LoginDTO;
import com.example.courier.dto.jwt.JwtClaims;
import com.example.courier.repository.PersonRepository;
import com.example.courier.repository.UserRepository;
import com.example.courier.service.auth.AuthService;
import com.example.courier.service.auth.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    private static final String TEST_EMAIL = "good@login.lt";
    private static final String TEST_PASSWORD = "encodedPassword";
    private static final String TEST_NAME = "Test user";
    private static final String TEST_ROLE = "USER";

    private final LoginDTO validLogin = new LoginDTO(TEST_EMAIL, TEST_PASSWORD);
    private final LoginDTO invalidLoginNullEmail = new LoginDTO(null, TEST_PASSWORD);
    private final LoginDTO invalidLoginNullPassword = new LoginDTO(TEST_EMAIL, null);
    private final JwtClaims mockUserClaims = new JwtClaims(TEST_EMAIL, TEST_ROLE, TEST_NAME, "mockAuthToken");

    private Person testUser;

    @Nested
    @DisplayName("Success cases")
    class SuccessCases {

        @BeforeEach
        void setUp() {
            testUser = createUser(TEST_EMAIL, TEST_PASSWORD, TEST_NAME, TEST_ROLE);
        }

        private Person createUser(String email, String password, String name, String role) {
            return new Person(name, email, password) {
                @Override public String getRole() {return role;}
            };
        }

        @Test
        @DisplayName("Success user login - should return success response")
        void login_validCredentials_works() {
            when(personRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(TEST_PASSWORD, testUser.getPassword())).thenReturn(true);
            when(jwtService.createToken(TEST_EMAIL, TEST_ROLE, TEST_NAME)).thenReturn("fakeJWT");
            when(jwtService.validateToken("fakeJWT")).thenReturn(mockUserClaims);
            when(jwtService.encryptAuthToken("mockAuthToken")).thenReturn("encryptedAuthToken");

            Map<String, String> result = authService.loginUser(validLogin, response);

            assertEquals("Login successfully.", result.get("message"));

            verify(personRepository).findByEmail(TEST_EMAIL);
            verify(passwordEncoder).matches(TEST_PASSWORD, "encodedPassword");
            verify(jwtService).createToken(TEST_EMAIL, TEST_ROLE, TEST_NAME);
            verify(jwtService).validateToken("fakeJWT");
            verify(jwtService).encryptAuthToken("mockAuthToken");

            verify(response, times(2)).addCookie(any(Cookie.class));
            verify(response).addCookie(argThat(cookie ->
                    "jwt".equals(cookie.getName()) &&
                    "fakeJWT".equals(cookie.getValue())));
            verify(response).addCookie(argThat(cookie ->
                    "authToken".equals(cookie.getName()) &&
                        "encryptedAuthToken".equals(cookie.getValue())));
        }
    }

    @Nested
    class FailureCases {
        private final Person testUser = new Person() {
            @Override
            public String getRole() {
                return "USER";
            }
        };

        @Test
        @DisplayName("User login, wrong credentials - should fail")
        void userLogin_invalidPassword_throwsBadCredentials() {
            testUser.setEmail(validLogin.email());
            testUser.setName("Test User");
            testUser.setPassword("encodedPassword");

            when(personRepository.findByEmail(validLogin.email())).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(validLogin.password(), testUser.getPassword())).thenReturn(false);

            assertThrows(RuntimeException.class, () ->
                    authService.loginUser(validLogin, response));

            verify(personRepository).findByEmail(validLogin.email());
            verify(passwordEncoder).matches(validLogin.password(), testUser.getPassword());
            verify(jwtService, never()).createToken(any(), any(), any());
            verify(response, never()).addCookie(any());
        }

        @Test
        @DisplayName("User login - fails when email not found")
        void loginUser_emailNotFound_throwsRuntimeException() {
            when(personRepository.findByEmail(validLogin.email())).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () ->
                    authService.loginUser(validLogin, response));

            verify(personRepository).findByEmail(validLogin.email());
            verify(passwordEncoder, never()).matches(validLogin.password(), testUser.getPassword());
            verify(jwtService, never()).createToken(any(), any(), any());
        }

        @Test
        @DisplayName("Login fails when JWT validation throws excption")
        void loginUser_jwtValidationFails_throwsRuntimeException() {
            testUser.setEmail(validLogin.email());
            testUser.setName("Test User");
            testUser.setPassword("encodedPassword");

            when(personRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(validLogin.password(), testUser.getPassword())).thenReturn(true);
            when(jwtService.createToken(testUser.getEmail(), testUser.getRole(), testUser.getName())).thenReturn("fakeJWT");
            when(jwtService.validateToken("fakeJWT")).thenThrow(new RuntimeException("Invalid jwt token"));

            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    authService.loginUser(validLogin, response));


            assertEquals("Unexpected error occurred during login", exception.getMessage());

            verify(personRepository).findByEmail(validLogin.email());
            verify(passwordEncoder).matches(validLogin.password(), testUser.getPassword());
            verify(jwtService).createToken(any(), any(), any());
            verify(jwtService).validateToken("fakeJWT");
        }

        @Test
        @DisplayName("Login fails when email is null")
        void login_nullEmail_throwsException() {
            assertThrows(RuntimeException.class, () ->
                    authService.loginUser(invalidLoginNullEmail, response));
        }

        @Test
        @DisplayName("Login fails when password is null")
        void login_nullPassword_throwsException() {
            assertThrows(RuntimeException.class, () ->
                    authService.loginUser(invalidLoginNullPassword, response));
        }
    }
}