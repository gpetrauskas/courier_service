package com.example.courier;

import com.example.courier.common.ApiResponseType;
import com.example.courier.domain.Admin;
import com.example.courier.domain.Courier;
import com.example.courier.domain.Person;
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

    private final RegistrationDTO validReg =
            new RegistrationDTO("Name Surname", "valid@example.com", "goodPassword1");
    private final RegistrationDTO invalidEmailReg =
            new RegistrationDTO("Name Surname", "invalid.example.com", "goodPassword1");
    private final RegistrationDTO shortPasswordReg =
            new RegistrationDTO("Name Surname", "valid@example.com", "short");
    private final RegistrationDTO noNumberPassword =
            new RegistrationDTO("Name Surname", "valid@example.com", "shoaaaaadWwrt");
    private final RegistrationDTO noUpperCasePassword =
            new RegistrationDTO("Name Surname", "valid@example.com", "goodpassword1");
    private final RegistrationDTO noLowerCasePassword =
            new RegistrationDTO("Name Surname", "valid@example.com", "GOODPASSWORD1");
    private final RegistrationDTO longPasswordReq =
            new RegistrationDTO("Name Surname", "valid@example.com", "Gw111OODasdadPASSWORD1");
    List<String> invalidEmails = List.of(
            "plaintext",
            "missing@domain",
            "invalid@.com",
            "@domain.com",
            "spaces @domain.com",
            "double..dots@domain.com",
            "invalid@domain..com",
            "user@com",
            "user@.com",
            "user@domain.c",
            "user@domain.123",
            "user@-domain.com"
    );
    private final Person tAdmin = new Admin();


    @Nested
    @DisplayName("Success cases")
    class SuccessCases {
        private final User mockUser = new User(validReg.name(), validReg.email(), validReg.password());
        private final Admin admin = new Admin();
        private final Courier mockCourier = new Courier();

        @BeforeEach
        void setupEntities() {
            admin.setEmail("admin@example.com");
            mockCourier.setName(validReg.name());
            mockCourier.setEmail(validReg.email());
            mockCourier.setPassword(validReg.password());

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

            verify(personService).checkIfPersonAlreadyExistsByEmail(validReg.email());
            verify(passwordEncoder).encode(validReg.password());
            verify(personRepository).save(argThat(user ->
                    user.getEmail().equals(mockUser.getEmail()) &&
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
            verify(personService).checkIfPersonAlreadyExistsByEmail(validReg.email());
            verify(passwordEncoder).encode(validReg.password());
            verify(personRepository).save(argThat(courier ->
                    courier.getEmail().equals(mockCourier.getEmail()) &&
                    courier.getPassword().equals("encodedPassword")));
        }
    }

    @Nested
    @DisplayName("Failure cases")
    class FailureCases {
        @Test
        @DisplayName("Should throw when email already exists")
        void register_rejectsEmailAlreadyExists() {
            when(personService.checkIfPersonAlreadyExistsByEmail(validReg.email())).thenReturn(true);

            assertThatThrownBy(() -> registrationService.registerUser(validReg))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Email %s is already registered".formatted(validReg.email()));

            verify(personService).checkIfPersonAlreadyExistsByEmail(validReg.email());
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
            assertThatThrownBy(() -> registrationService.registerUser(shortPasswordReg))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Password length must be between 8-16 characters");

            verify(passwordValidator).validatePassword(shortPasswordReg.password());
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
            when(personService.checkIfPersonAlreadyExistsByEmail(anyString())).thenReturn(false);

            assertThrows(RuntimeException.class,
                    () -> registrationService.registerUser(new RegistrationDTO("name", null, "password11111")));

            verify(personRepository, never()).save(any());
        }

        @Test
        @DisplayName("Rejects - passowrd must have a number")
        void register_rejectsMissingNumberInPassword() {
            assertThatThrownBy(() -> registrationService.registerUser(noNumberPassword))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Password must contain at least one number");

            verify(personRepository, never()).save(any());
        }

        @Test
        @DisplayName("Fails on concurrent registration with same email")
        void register_rejectOnParallelRequestWithSameEmail() {
            when(personService.checkIfPersonAlreadyExistsByEmail(any()))
                    .thenReturn(false)
                    .thenReturn(true);

            assertDoesNotThrow(() -> registrationService.registerUser(validReg));

            assertThatThrownBy(() -> registrationService.registerUser(validReg))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Email " + validReg.email() + " is already registered");

            verify(personService, times(2)).checkIfPersonAlreadyExistsByEmail(validReg.email());
            verify(personRepository, times(1)).save(any());
        }

        @Test
        void registerUser_rejectsPasswordWithoutUpperCase() {
            assertThatThrownBy(() -> registrationService.registerUser(noUpperCasePassword))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Password must contain at least one uppercase letter");
        }

        @Test
        void registerUser_rejectsPasswordWithoutLowerCase() {
            assertThatThrownBy(() -> registrationService.registerUser(noLowerCasePassword))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Password must contain at least one lowercase letter");
        }

        @Test
        void registerUser_rejectsPasswordToLong() {
            assertThatThrownBy(() -> registrationService.registerUser(longPasswordReq))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Password length must be between 8-16 characters");
        }

        @Test
        void registerUser_rejectsInvalidEmailFormats() {
            invalidEmails.forEach(e -> {
                RegistrationDTO invalidE = new RegistrationDTO("name", e, "valIdPAss123");

                assertThatThrownBy(() -> registrationService.registerUser(invalidE))
                        .isInstanceOf(ValidationException.class)
                        .hasMessageContaining("Email is not valid");
            });

            verify(registrationValidator, times(invalidEmails.size())).validateUserRegistration(any());
        }

    }
}
