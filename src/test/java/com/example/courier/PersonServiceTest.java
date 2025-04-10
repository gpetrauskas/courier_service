package com.example.courier;

import com.example.courier.domain.Person;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.person.PasswordChangeDTO;
import com.example.courier.repository.PersonRepository;
import com.example.courier.service.person.PersonServiceImpl;
import com.example.courier.validation.PasswordValidator;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @Mock private Authentication authentication;
    @Mock private SecurityContext securityContext;
    @Mock private PersonRepository personRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private PasswordValidator passwordValidator;

    private Person testPerson;

    @InjectMocks private PersonServiceImpl personService;

    @BeforeEach
    void setUp() {
        testPerson = new Person() {
            @Override
            public String getRole() {
                return "USER";
            }
        };
        testPerson.setEmail("test@example.com");
        testPerson.setPassword("encodedCurrentPassword");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(testPerson);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(personRepository.findById(any())).thenReturn(Optional.of(testPerson));
    }

    @Test
    void changePassword_successfulUpdate() {
        PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO(
                "newPassword1", "currentPassword1"
        );

        when(personRepository.findById(any())).thenReturn(Optional.of(testPerson));
        when(passwordEncoder.matches(passwordChangeDTO.currentPassword(), testPerson.getPassword())).thenReturn(true);
        doNothing().when(passwordValidator).validatePassword(passwordChangeDTO.newPassword());
        when(passwordEncoder.encode(passwordChangeDTO.newPassword())).thenReturn("encodedNewPassword");

        ApiResponseDTO responseDTO = personService.changePassword(passwordChangeDTO);

        assertEquals("success", responseDTO.status());
        verify(personRepository).save(argThat(p ->
                p.getPassword().equals("encodedNewPassword") &&
                p.getEmail().equals("test@example.com")
        ));
    }

    @Test
    void changePassword_wrongCurrentPassword_shouldFail() {
        PasswordChangeDTO dto = new PasswordChangeDTO(
                "wrongpassword",
                "newValidPassword1"
        );

        when(passwordEncoder.matches(dto.currentPassword(), testPerson.getPassword()))
                .thenReturn(false);

        assertThrows(ValidationException.class, () ->
                personService.changePassword(dto)
        );
        verify(personRepository, never()).save(any());
    }

    @Test
    void changePassword_invalidNewPassword_shouldFail() {
        PasswordChangeDTO dto = new PasswordChangeDTO(
                "newInvalid",
                "currentPassword1"
        );

        when(passwordEncoder.matches(dto.currentPassword(), testPerson.getPassword())).thenReturn(true);
        doThrow(new ValidationException("Invalid password")).when(passwordValidator)
                .validatePassword(dto.newPassword());
        assertThrows(ValidationException.class, () ->
                personService.changePassword(dto));
    }

}
