package com.example.courier;

import com.example.courier.domain.*;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.CourierDTO;
import com.example.courier.dto.mapper.BanHistoryMapper;
import com.example.courier.dto.mapper.PersonMapper;
import com.example.courier.dto.request.PersonDetailsUpdateRequest;
import com.example.courier.dto.request.person.BanActionRequestDTO;
import com.example.courier.dto.request.person.PasswordChangeDTO;
import com.example.courier.dto.request.person.UserEditDTO;
import com.example.courier.dto.response.BanHistoryDTO;
import com.example.courier.dto.response.person.AdminProfileResponseDTO;
import com.example.courier.dto.response.person.PersonResponseDTO;
import com.example.courier.dto.response.person.UserResponseDTO;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.BanHistoryRepository;
import com.example.courier.repository.PersonRepository;
import com.example.courier.service.person.PersonServiceImpl;
import com.example.courier.service.person.strategy.AdminInfoStrategy;
import com.example.courier.service.person.strategy.PersonInfoStrategy;
import com.example.courier.service.person.strategy.UserInfoStrategy;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.validation.person.EmailValidator;
import com.example.courier.validation.PasswordValidator;
import com.example.courier.validation.person.NameValidator;
import com.example.courier.validation.person.PersonDetailsValidator;
import com.example.courier.validation.person.PhoneValidator;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @Mock private Authentication authentication;
    @Mock private SecurityContext securityContext;
    @Mock private PersonRepository personRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private PasswordValidator passwordValidator;
    @Mock private PersonMapper personMapper;
    @Mock private BanHistoryRepository banHistoryRepository;
    @Mock private BanHistoryMapper banHistoryMapper;
    @Mock private PhoneValidator phoneValidator;
    @Mock private CurrentPersonService currentPersonService;
    @Mock private UserInfoStrategy userInfoStrategy;
    @Mock private AdminInfoStrategy adminInfoStrategy;
    @Mock private PersonDetailsValidator personDetailsValidator;
    @Mock private EmailValidator emailValidator;
    @Mock private NameValidator nameValidator;

    private Person testPerson;

    private PersonServiceImpl personService;

    @BeforeEach
    void setup() {
        List<PersonInfoStrategy> strategyList = List.of(userInfoStrategy, adminInfoStrategy);
        personService = new PersonServiceImpl(personRepository, personMapper, banHistoryRepository, banHistoryMapper,
                phoneValidator, passwordValidator, passwordEncoder, currentPersonService, strategyList,
                personDetailsValidator, emailValidator, nameValidator);
    }

    @Nested
    class ChangePassword {
        @BeforeEach
        void setUp() {
            testPerson = createMockPerson(1L, "name", "USER");
            testPerson.setEmail("test@example.com");

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

    private static Person createMockPerson(Long id, String name, String role) {
        return new Person() {
            @Override public Long getId() { return id; }
            @Override public String getName() { return name; }
            @Override public String getRole() { return role; }
        };
    }
}
