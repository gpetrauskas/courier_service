package com.example.courier;

import com.example.courier.common.ApiResponseType;
import com.example.courier.domain.Admin;
import com.example.courier.domain.Courier;
import com.example.courier.domain.User;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.RegistrationDTO;
import com.example.courier.repository.CourierRepository;
import com.example.courier.repository.PersonRepository;
import com.example.courier.repository.UserRepository;
import com.example.courier.service.RegistrationService;
import com.example.courier.service.auth.AuthService;
import com.example.courier.service.auth.JwtService;
import com.example.courier.service.person.PersonService;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.validation.PasswordValidator;
import com.example.courier.validation.RegistrationValidator;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {

    @Mock
    private AuthService authService;
    @Mock
    private PersonRepository personRepository;
    @Spy
    private RegistrationValidator registrationValidator;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CourierRepository courierRepository;
    @Mock
    private JwtService jwtService;
    @Spy
    private PasswordValidator passwordValidator;
    @Mock
    private PersonService personService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CurrentPersonService currentPersonService;

    @InjectMocks
    private RegistrationService registrationService;
    
    private static final String DEFAULT_NAME = "Name Surname";
    private static final String DEFAULT_EMAIL = "valid@example.com";
    private static final String DEFAULT_PASSWORD = "goodPassword1";

    private final RegistrationDTO validReg = new RegistrationDTO(DEFAULT_NAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);
    private final RegistrationDTO invalidEmailReg = new RegistrationDTO(DEFAULT_NAME, "invalid.example.com", DEFAULT_PASSWORD);

    private RegistrationDTO regWithPassword(String password) {
        return new RegistrationDTO(DEFAULT_NAME, DEFAULT_EMAIL, password);
    }

    private RegistrationDTO regWithEmail(String email) {
        return new RegistrationDTO(DEFAULT_NAME, email, DEFAULT_PASSWORD);
    }

    @Nested
    @DisplayName("Success cases")
    class SuccessCases {
        private User mockUser;
        private Admin admin;
        private Courier mockCourier;

        @BeforeEach
        void setupEntities() {
            mockUser = new User(DEFAULT_NAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);

            admin = new Admin();
            admin.setEmail("admin@example.com");

            mockCourier = new Courier();
            mockCourier.setName(DEFAULT_NAME);
            mockCourier.setEmail(DEFAULT_EMAIL);
            mockCourier.setPassword(DEFAULT_PASSWORD);

            when(personService.checkIfPersonAlreadyExistsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(validReg.password())).thenReturn("encodedPassword");
        }

        @Test
        @DisplayName("User registration - should return success response")
        void registerUser_success() {
            when(personRepository.save(any(User.class))).thenReturn(mockUser);

            ApiResponseDTO response = registrationService.registerUser(validReg);

            assertThat(response)
                    .usingRecursiveComparison()
                    .ignoringFields("timestamp")
                    .isEqualTo(ApiResponseType.USER_REGISTRATION_SUCCESS.apiResponseDTO());

            verify(personService).checkIfPersonAlreadyExistsByEmail(DEFAULT_EMAIL);
            verify(passwordEncoder).encode(DEFAULT_PASSWORD);
            verify(personRepository).save(argThat(user ->
                    user.getEmail().equals(DEFAULT_EMAIL) &&
                    user.getPassword().equals("encodedPassword")));
        }

        @Test
        @DisplayName("Courier registration - should include admin email in API response")
        void registerCourier_Success() {
            when(currentPersonService.getCurrentPerson()).thenReturn(admin);
            when(personRepository.save(any(Courier.class))).thenReturn(mockCourier);

            ApiResponseDTO response = registrationService.registerCourier(validReg);

            assertThat(response)
                    .usingRecursiveComparison()
                    .ignoringFields("timestamp")
                    .isEqualTo(ApiResponseType.COURIER_REGISTRATION_SUCCESS.withParams(admin.getEmail()));

            verify(currentPersonService).getCurrentPerson();
            verify(personService).checkIfPersonAlreadyExistsByEmail(DEFAULT_EMAIL);
            verify(passwordEncoder).encode(DEFAULT_PASSWORD);
            verify(personRepository).save(argThat(courier ->
                    courier.getEmail().equals(DEFAULT_EMAIL) &&
                    courier.getPassword().equals("encodedPassword")));
        }
    }

    @Nested
    @DisplayName("Failure cases")
    class FailureCases {
        private final List<String> invalidEmails = List.of(
                "plaintext", "missing@domain", "invalid@.com", "@domain.com", "spaces @domain.com",
                "double..dots@domain.com", "invalid@domain..com", "user@com", "user@.com", "user@domain.c",
                "user@domain.123", "user@-domain.com"
        );

        @Test
        @DisplayName("Should throw when email already exists")
        void register_rejectsEmailAlreadyExists() {
            when(personService.checkIfPersonAlreadyExistsByEmail(DEFAULT_EMAIL)).thenReturn(true);

            assertThatThrownBy(() -> registrationService.registerUser(validReg))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Email %s is already registered".formatted(DEFAULT_EMAIL));

            verify(personService).checkIfPersonAlreadyExistsByEmail(DEFAULT_EMAIL);
            verify(personRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should reject invalid email format")
        void register_rejectsInvalidEmail() {
            doThrow(new ValidationException("Invalid email format"))
                    .when(registrationValidator).validateUserRegistration(invalidEmailReg);

            assertThatThrownBy(() -> registrationService.registerUser(invalidEmailReg))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Invalid email format");

            verify(registrationValidator).validateUserRegistration(invalidEmailReg);
            verify(personRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should reject - password to short")
        void register_rejectsPasswordToShort() {
            assertThatThrownBy(() -> registrationService.registerUser(regWithPassword("short")))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Password length must be between 8-16 characters");

            verify(passwordValidator).validatePassword("short");
            verify(personRepository, never()).save(any());
        }

        @Test
        @DisplayName("Rejects null RegistrationDTO")
        void register_rejectsNullDTO() {
            assertThrows(RuntimeException.class,
                    () -> registrationService.registerUser(null));

            verifyNoInteractions(personRepository);
        }

        @Test
        @DisplayName("Rejects partly null RegistrationDTO")
        void register_RejectNullFields() {
            when(personService.checkIfPersonAlreadyExistsByEmail(DEFAULT_EMAIL)).thenReturn(false);

            assertThrows(RuntimeException.class,
                    () -> registrationService.registerUser(regWithEmail(null)));

            verify(personRepository, never()).save(any());
        }

        @Test
        @DisplayName("Rejects - passowrd must have a number")
        void register_rejectsMissingNumberInPassword() {
            assertThatThrownBy(() -> registrationService.registerUser(regWithPassword("noNumberPass")))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Password must contain at least one number");

            verify(personRepository, never()).save(any());
        }

        @Test
        @DisplayName("Fails on concurrent registration with same email")
        void register_rejectOnParallelRequestWithSameEmail() {
            when(personService.checkIfPersonAlreadyExistsByEmail(DEFAULT_EMAIL))
                    .thenReturn(false)
                            .thenReturn(true);

            assertDoesNotThrow(() -> registrationService.registerUser(validReg));

            assertThatThrownBy(() -> registrationService.registerUser(validReg))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Email " + DEFAULT_EMAIL + " is already registered");

            verify(personService, times(2)).checkIfPersonAlreadyExistsByEmail(DEFAULT_EMAIL);
            verify(personRepository, times(1)).save(any());
        }

        @Test
        void registerUser_rejectsPasswordWithoutUpperCase() {
            assertThatThrownBy(() -> registrationService.registerUser(regWithPassword("nouppercasepass1")))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Password must contain at least one uppercase letter");
        }

        @Test
        void registerUser_rejectsPasswordWithoutLowerCase() {
            assertThatThrownBy(() -> registrationService.registerUser(regWithPassword("NOLOWERCASEPASS1")))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Password must contain at least one lowercase letter");
        }

        @Test
        void registerUser_rejectsPasswordToLong() {
            assertThatThrownBy(() -> registrationService.registerUser(regWithPassword("pa55wOrDmIghtbE4littleb1ttolong")))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Password length must be between 8-16 characters");
        }

        @Test
        void registerUser_rejectsInvalidEmailFormats() {
            invalidEmails.forEach(email -> {
                RegistrationDTO invalidE = new RegistrationDTO(DEFAULT_NAME, email, DEFAULT_PASSWORD);

                assertThatThrownBy(() -> registrationService.registerUser(invalidE))
                        .isInstanceOf(ValidationException.class)
                        .hasMessageContaining("Email is not valid");
            });

            verify(registrationValidator, times(invalidEmails.size())).validateUserRegistration(any());
        }

    }
}
