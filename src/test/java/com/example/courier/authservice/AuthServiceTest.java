package com.example.courier.authservice;

import com.example.courier.domain.Person;
import com.example.courier.dto.LoginDTO;
import com.example.courier.dto.jwt.JwtClaims;
import com.example.courier.service.auth.AuthService;
import com.example.courier.service.auth.JwtService;
import com.example.courier.service.person.PersonService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private PersonService personService;
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

    @BeforeEach
    void setup() {
        testUser = new Person(TEST_NAME, TEST_EMAIL, TEST_PASSWORD) {
            @Override public String getRole() {return TEST_ROLE;}
        };
    }

    @Nested
    @DisplayName("Success cases")
    class SuccessCases {
        @Test
        @DisplayName("Success user login - should return success response")
        void login_validCredentials_works() {
            mockUserLookup();
            mockValidPassword(true);
            when(jwtService.createToken(TEST_EMAIL, TEST_ROLE, TEST_NAME)).thenReturn("fakeJWT");
            when(jwtService.validateToken("fakeJWT")).thenReturn(mockUserClaims);
            when(jwtService.encryptAuthToken("mockAuthToken")).thenReturn("encryptedAuthToken");

            var result = authService.loginUser(validLogin, response);

            assertEquals("Logged in successfully.", result.message());

            verify(personService).findByUsername(TEST_EMAIL);
            verify(passwordEncoder).matches(TEST_PASSWORD, "encodedPassword");
            verify(jwtService).createToken(TEST_EMAIL, TEST_ROLE, TEST_NAME);
            verify(jwtService).validateToken("fakeJWT");
            verify(jwtService).encryptAuthToken("mockAuthToken");

            verify(response, times(2)).addCookie(any(Cookie.class));
            verifyCookie("jwt", "fakeJWT");
            verifyCookie("authToken", "encryptedAuthToken");
        }

        private void verifyCookie(String name, String value) {
            verify(response).addCookie(argThat(cookie ->
                    name.equals(cookie.getName()) &&
                    value.equals(cookie.getValue())
            ));
        }
    }

    @Nested
    @DisplayName("Failure cases")
    class FailureCases {
        @Test
        @DisplayName("User login, wrong password throws exception")
        void userLogin_invalidPassword_throwsException() {
            mockUserLookup();
            mockValidPassword(false);

            assertThrows(RuntimeException.class, () ->
                    authService.loginUser(validLogin, response));

            verify(personService).findByUsername(TEST_EMAIL);
            verify(passwordEncoder).matches(validLogin.password(), TEST_PASSWORD);
            verifyNoInteractions(jwtService, response);
        }

        @Test
        @DisplayName("User login - fails when email not found")
        void loginUser_emailNotFound_throwsRuntimeException() {
            when(personService.findByUsername(TEST_EMAIL)).thenReturn(null);

            assertThrows(RuntimeException.class, () ->
                    authService.loginUser(validLogin, response));

            verify(personService).findByUsername(TEST_EMAIL);
            verify(passwordEncoder, never()).matches(validLogin.password(), TEST_PASSWORD);
            verifyNoInteractions(jwtService);
        }

        @Test
        @DisplayName("Login fails when JWT validation throws excption")
        void loginUser_jwtValidationFails_throwsRuntimeException() {
            mockUserLookup();
            mockValidPassword(true);
            when(jwtService.createToken(TEST_EMAIL, TEST_ROLE, TEST_NAME)).thenReturn("fakeJWT");
            when(jwtService.validateToken("fakeJWT")).thenThrow(new RuntimeException("Invalid jwt token"));

            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    authService.loginUser(validLogin, response));


            assertEquals("Unexpected error occurred during login", exception.getMessage());
            verify(personService).findByUsername(TEST_EMAIL);
            verify(passwordEncoder).matches(validLogin.password(), TEST_PASSWORD);
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

    private void mockUserLookup() {
        when(personService.findByUsername(TEST_EMAIL)).thenReturn(testUser);
    }

    private void mockValidPassword(boolean isMatch) {
        when(passwordEncoder.matches(TEST_PASSWORD, testUser.getPassword())).thenReturn(isMatch);
    }
}