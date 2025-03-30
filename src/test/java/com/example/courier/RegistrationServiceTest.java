package com.example.courier;

import com.example.courier.domain.Courier;
import com.example.courier.domain.User;
import com.example.courier.dto.RegistrationDTO;
import com.example.courier.repository.CourierRepository;
import com.example.courier.repository.PersonRepository;
import com.example.courier.repository.UserRepository;
import com.example.courier.service.RegistrationService;
import com.example.courier.service.auth.AuthService;
import com.example.courier.service.auth.JwtService;
import com.example.courier.service.person.PersonService;
import com.example.courier.validation.RegistrationValidator;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {

    @Mock
    private AuthService authService;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private RegistrationValidator registrationValidator;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CourierRepository courierRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private PersonService personService;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RegistrationService registrationService;

    private final RegistrationDTO validReg =
            new RegistrationDTO("Name Surname", "valid@example.com", "goodPassword1");
    private final RegistrationDTO invalidEmailReg =
            new RegistrationDTO("Name Surname", "invalid.example.com", "goodPassword1");
    private final RegistrationDTO shortPasswordReg =
            new RegistrationDTO("Name Surname", "valid@example.com", "short");


    @Test
    @DisplayName("Successful registration saves user with encoded password")
    void registerUser_success() {
        when(personService.checkIfPersonAlreadyExistsByEmail(validReg.email())).thenReturn(false);
        when(passwordEncoder.encode(validReg.password())).thenReturn("encodedPassword");

        User mockUser = new User(validReg.name(), validReg.email(), "encodedPassword");
        when(personRepository.save(any(User.class))).thenReturn(mockUser);

        assertDoesNotThrow(() -> registrationService.registerUser(validReg));

        verify(personService).checkIfPersonAlreadyExistsByEmail(validReg.email());
        verify(passwordEncoder).encode(validReg.password());
        verify(personRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Rejects email already exists")
    void registerUser_rejectsEmailAlreadyExists() {
        when(personService.checkIfPersonAlreadyExistsByEmail(validReg.email())).thenReturn(true);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> registrationService.registerUser(validReg));

        assertEquals("Email %s is already registered".formatted(validReg.email()), exception.getMessage());
        verify(personService).checkIfPersonAlreadyExistsByEmail(validReg.email());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Wraps repository errors during user registration")
    void registerUser_wrapsRepositoryErrors() {
        when(personService.checkIfPersonAlreadyExistsByEmail(validReg.email())).thenReturn(false);
        when(passwordEncoder.encode(validReg.password())).thenReturn("encodedPassword");
        when(personRepository.save(any())).thenThrow(new RuntimeException("DB error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
        () -> registrationService.registerUser(validReg));

        assertEquals("DB error", exception.getMessage());
    }

    @Test
    @DisplayName("Creates user with correct details")
    void registerUser_createCorrectUser() {
        when(personService.checkIfPersonAlreadyExistsByEmail(validReg.email())).thenReturn(false);
        when(passwordEncoder.encode(validReg.password())).thenReturn("encodedPassword");

        registrationService.registerUser(validReg);

        verify(personRepository).save(argThat(user ->
                user.getName().equals(validReg.name()) &&
                user.getEmail().equals(validReg.email()) &&
                user.getPassword().equals("encodedPassword")
        ));
    }

    @Test
    @DisplayName("Admin can register courier")
    void registerCourier_success() {
        when(personService.checkIfPersonAlreadyExistsByEmail(validReg.email())).thenReturn(false);
        when(passwordEncoder.encode(validReg.password())).thenReturn("encodedPassword");

        registrationService.registerCourier(validReg);

        verify(personRepository).save(any(Courier.class));
        verify(passwordEncoder).encode(validReg.password());
    }

    @Test
    @DisplayName("Rolls back when save fails")
    void registerUser_rollsBackOnFailure() {
        when(personService.checkIfPersonAlreadyExistsByEmail(any())).thenReturn(false);
        when(personRepository.save(any())).thenThrow(new RuntimeException("DB failure"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> registrationService.registerUser(validReg));

        verify(personRepository).save(any());
        assertEquals("DB failure", exception.getMessage());
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    @DisplayName("Throws validation exception when email is invalid")
    void registerUser_rejectsInvalidEmail() {
        doThrow(new ValidationException("Invalid email"))
                .when(registrationValidator).validateUserRegistration(invalidEmailReg);

        assertThrows(ValidationException.class,
                () -> registrationService.registerUser(invalidEmailReg));

        verify(registrationValidator).validateUserRegistration(invalidEmailReg);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Throws validation exception when password is too short")
    void registerUser_rejectsShortPassword() {
        doThrow(new ValidationException("Password too short"))
                .when(registrationValidator).validateUserRegistration(shortPasswordReg);

        assertThrows(ValidationException.class,
                () -> registrationService.registerUser(shortPasswordReg));

        verify(registrationValidator).validateUserRegistration(shortPasswordReg);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Courier registration fails if email exists")
    void registerCourier_rejectsEmailAlreadyExists() {
        when(personService.checkIfPersonAlreadyExistsByEmail(validReg.email())).thenReturn(true);

        assertThrows(ValidationException.class,
                () -> registrationService.registerCourier(validReg));

        verify(courierRepository, never()).save(any());
    }

    @Test
    @DisplayName("Courier registration fails email invalid")
    void registerUser_failsIfEmailInvalid() {
        doThrow(new ValidationException("Invalid email"))
                .when(registrationValidator).validateUserRegistration(invalidEmailReg);

        assertThrows(ValidationException.class,
                () -> registrationService.registerCourier(invalidEmailReg));
    }

    @Test
    @DisplayName("Courier registration wraps repository errors")
    void registerCourier_wrapsRepositoryErrors() {
        when(personService.checkIfPersonAlreadyExistsByEmail(validReg.email())).thenReturn(false);
        when(passwordEncoder.encode(validReg.password())).thenReturn("encodedPassword");
        when(personRepository.save(any())).thenThrow(new RuntimeException("DB error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> registrationService.registerCourier(validReg));

        assertTrue(exception.getMessage().contains("DB error"));
    }

    @Test
    @DisplayName("Rejects null RegistrationDTO")
    void registerUser_rejectsNullDTO() {
        assertThrows(RuntimeException.class,
                () -> registrationService.registerUser(null));
    }

    @Test
    @DisplayName("Rejects registrationDTO with null fields")
    void registerUser_rejectsNullFields() {
        RegistrationDTO dto = new RegistrationDTO("name", null, "passwordNotnull123");

        doThrow(new ValidationException("Fields cannot be null"))
                .when(registrationValidator).validateUserRegistration(dto);

        assertThrows(ValidationException.class,
                () -> registrationService.registerUser(dto));

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Fails when concurrent registration with same email")
    void registerUser_failsOnConcurrentDuplicateEmail() {
        when(personService.checkIfPersonAlreadyExistsByEmail(validReg.email()))
                .thenReturn(false)
                .thenReturn(true);

        assertDoesNotThrow(() -> registrationService.registerUser(validReg));

        assertThrows(ValidationException.class, () -> registrationService.registerUser(validReg));
    }

    @Test
    @DisplayName("Rejects passowrd without numbers")
    void registerUser_rejectsPasswordWithoutNumbers() {
        doThrow(new ValidationException("Password must contain numbers"))
                .when(registrationValidator).validateUserRegistration(validReg);

        assertThrows(ValidationException.class, () -> registrationService.registerUser(validReg));
    }

}
