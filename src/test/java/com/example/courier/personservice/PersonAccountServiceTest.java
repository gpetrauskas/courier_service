package com.example.courier.personservice;
import com.example.courier.domain.User;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.person.PasswordChangeDTO;
import com.example.courier.dto.response.person.PersonResponseDTO;
import com.example.courier.dto.response.person.UserResponseDTO;
import com.example.courier.exception.StrategyNotFoundException;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.repository.PersonRepository;
import com.example.courier.service.person.account.PersonAccountService;
import com.example.courier.service.person.strategy.PersonInfoStrategyResolver;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.service.validation.PersonValidationService;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonAccountServiceTest {

    @Mock private PersonRepository personRepository;
    @Mock private CurrentPersonService currentPersonService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private PersonValidationService validationService;
    @Mock private PersonInfoStrategyResolver strategyResolver;

    @InjectMocks private PersonAccountService personAccountService;

    private User testUser;
    private UserResponseDTO testUserResponse;
    private PasswordChangeDTO validPasswordChangeDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setPassword("encodedOldPassword");
        testUserResponse = new UserResponseDTO("John Doe", null, null, null, null, true, 2);
        validPasswordChangeDto = new PasswordChangeDTO("NewValidPassword123", "oldPassword!");
    }

    /*
    * My info test
    */
    @Test
    void myInfo_shouldReturnPersonResponseFromResolver() {
        when(currentPersonService.getCurrentPerson()).thenReturn(testUser);
        when(strategyResolver.resolve(testUser)).thenReturn(testUserResponse);

        PersonResponseDTO result = personAccountService.myInfo();

        assertEquals(testUserResponse, result);
        verify(currentPersonService).getCurrentPerson();
        verify(strategyResolver).resolve(testUser);
    }

    @Test
    void myInfo_shouldThrowException_whenNoStrategyFound() {
        when(currentPersonService.getCurrentPerson()).thenReturn(testUser);
        when(strategyResolver.resolve(testUser)).thenThrow(new StrategyNotFoundException("No strategy found"));

        assertThrows(StrategyNotFoundException.class, () -> personAccountService.myInfo());

        verify(strategyResolver).resolve(testUser);
    }

    @Test
    void myInfo_shouldThrowUnauthorized_whenNotAuthenticated() {
        when(currentPersonService.getCurrentPerson()).thenThrow(new UnauthorizedAccessException("Not logged in."));

        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class,
                () -> personAccountService.myInfo());

        assertEquals("Not logged in.", exception.getMessage());
    }

    /*
    * Change password
    */
    @Test
    @Transactional
    void changePassword_shouldSucceed_whenValid() {
        when(currentPersonService.getCurrentPerson()).thenReturn(testUser);
        when(passwordEncoder.matches("oldPassword!", "encodedOldPassword")).thenReturn(true);
        doNothing().when(validationService).validatePassword("NewValidPassword123");
        when(passwordEncoder.encode("NewValidPassword123")).thenReturn("encodedNewPassword");

        ApiResponseDTO response = personAccountService.changePassword(validPasswordChangeDto);

        assertEquals("success", response.status());
        assertEquals("Password updated successfully.", response.message());
        assertEquals("encodedNewPassword", testUser.getPassword());
        verify(personRepository).save(testUser);
    }

    @Test
    void changePassword_shouldFail_whenCurrentPasswordIncorrect() {
        PasswordChangeDTO dto = new PasswordChangeDTO("NewValidPassword123", "wrongPassword");

        when(currentPersonService.getCurrentPerson()).thenReturn(testUser);
        when(passwordEncoder.matches("wrongPassword", "encodedOldPassword")).thenReturn(false);

        assertThrows(ValidationException.class, () -> personAccountService.changePassword(dto));

        verify(passwordEncoder).matches("wrongPassword", "encodedOldPassword");
        verifyNoInteractions(validationService, personRepository);
    }

    @Test
    void changePassword_shouldFail_whenNewPasswordInvalid() {
        PasswordChangeDTO dto = new PasswordChangeDTO("short", "oldPassword");

        when(currentPersonService.getCurrentPerson()).thenReturn(testUser);
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        doThrow(new ValidationException("Invalid password"))
                .when(validationService).validatePassword("short");

        assertThrows(ValidationException.class, () -> personAccountService.changePassword(dto));

        verify(validationService).validatePassword("short");
        verifyNoInteractions(personRepository);
    }

    @Test
    void changePassword_shouldBeTransactional() throws NoSuchMethodException {
        assertNotNull(PersonAccountService.class
                .getMethod("changePassword", PasswordChangeDTO.class)
                .getAnnotation(Transactional.class));
    }

    /*
    * Null cases
    */
    @Test
    void changePassword_shouldHandleNullDto() {
        assertThrows(ValidationException.class, () -> personAccountService.changePassword(null));
    }

    @Test
    void changePassword_shouldHandleNullCurrentPassword() {
        PasswordChangeDTO dto = new PasswordChangeDTO("NewValidPassword123", null);

        assertThrows(ValidationException.class, () -> personAccountService.changePassword(dto));
    }

    @Test
    void changePassword_shouldHandleNullNewPassword() {
        PasswordChangeDTO dto = new PasswordChangeDTO(null, "oldPassword");

        assertThrows(ValidationException.class, () -> personAccountService.changePassword(dto));
    }
}