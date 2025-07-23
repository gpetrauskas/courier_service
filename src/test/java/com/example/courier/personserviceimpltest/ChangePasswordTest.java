package com.example.courier.personserviceimpltest;

import com.example.courier.domain.Person;
import com.example.courier.dto.request.person.PasswordChangeDTO;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.PersonRepository;
import com.example.courier.service.person.PersonServiceImpl;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.service.validation.PersonValidationService;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChangePasswordTest {

    @Mock private CurrentPersonService currentPersonService;
    @Mock private PersonRepository personRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private PersonValidationService personValidationService;

    @InjectMocks private PersonServiceImpl personService;

    private Person person;

    @BeforeEach
    void setup() {
        person = new Person() {
            @Override public String getRole() { return "USER"; }
            @Override public Long getId() { return 1L; }
        };
        person.setPassword("oldPassword123");

        when(currentPersonService.getCurrentPersonId()).thenReturn(1L);
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
    }

    @Test
    @DisplayName("successfully change password")
    void shouldSuccessWithValidPassword() {
        PasswordChangeDTO dto = new PasswordChangeDTO("currentPassword123", "oldPassword123");
        when(passwordEncoder.matches(dto.currentPassword(), person.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(dto.newPassword())).thenReturn("encodedNewPass123");

        var response = personService.changePassword(dto);

        assertEquals("success", response.status());
        assertEquals("encodedNewPass123", person.getPassword());
        verify(personValidationService).validatePassword(dto.newPassword());
        verify(personRepository).save(person);
    }

    @Test
    @DisplayName("should fail when current password does not match")
    void shouldFailWhenCurrentPasswordDoesNotMatch() {
        var dto = new PasswordChangeDTO("nonono", "newPassword123");

        when(passwordEncoder.matches(dto.currentPassword(), person.getPassword())).thenReturn(false);

        assertThrows(ValidationException.class, () -> personService.changePassword(dto));

        verify(personRepository, never()).save(any());
    }

    @Test
    @DisplayName("should fail when new password is invalid")
    void shouldFailWhenNewPasswordIsInvalid() {
        var dto = new PasswordChangeDTO("oldPassword123", "invalidpass");

        when(passwordEncoder.matches(dto.currentPassword(), person.getPassword())).thenReturn(true);
        doThrow(new ValidationException("Password must contain at least one upper letter"))
                .when(personValidationService).validatePassword(dto.newPassword());

        ValidationException ex = assertThrows(ValidationException.class, () -> personService.changePassword(dto));

        assertEquals("Password must contain at least one upper letter", ex.getMessage());
        verify(personRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw when user not found")
    void shouldThrowWhenUserNotFound() {
        var dto = new PasswordChangeDTO("newPassword123", "oldPassword123");

        when(personRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> personService.changePassword(dto));
    }
}
