package com.example.courier;

import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.mapper.BanHistoryMapper;
import com.example.courier.dto.mapper.PersonMapper;
import com.example.courier.dto.request.PersonDetailsUpdateRequest;
import com.example.courier.dto.request.person.PasswordChangeDTO;
import com.example.courier.dto.response.person.AdminPersonResponseDTO;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.BanHistoryRepository;
import com.example.courier.repository.PersonRepository;
import com.example.courier.service.person.PersonServiceImpl;
import com.example.courier.specification.person.PersonSpecificationBuilder;
import com.example.courier.validation.PasswordValidator;
import com.example.courier.validation.PhoneValidator;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

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

    private Person testPerson;

    private PersonServiceImpl personService;

    @BeforeEach
    void setup() {
        personService = new PersonServiceImpl(personRepository, personMapper, banHistoryRepository, banHistoryMapper,
                phoneValidator, passwordValidator, passwordEncoder);
    }

    @Nested
    class FindAllPaginated {
        private Person createMockPerson(Long id, String name, String role) {
            return new Person() {
                @Override
                public String getRole() {
                    return role;
                }
                @Override
                public Long getId() {
                    return id;
                }
                @Override
                public String getName() {
                    return name;
                }
            };
        }

        @Test
        @DisplayName("should return all users paginated")
        void shouldReturnAllUsersPaginated() {
            when(personRepository.findAll(ArgumentMatchers.<Specification<Person>>any(), any(Pageable.class))).thenReturn(
                    new PageImpl<>(List.of(mock(Person.class), mock(Person.class)))
            );
            when(personMapper.toAdminPersonResponseDTO(any(Person.class))).thenReturn(mock(AdminPersonResponseDTO.class));

            var result = personService.findAllPaginated(0, 2, "", "", "id", "asc");

            assertNotNull(result);
            assertEquals(2, result.data().size());
        }

        @Test
        @DisplayName("should return empty lsit")
        void shouldReturnEmptyListWhenNoPersonFound() {
            when(personRepository.findAll(ArgumentMatchers.<Specification<Person>>any(), any(Pageable.class)))
                    .thenReturn(Page.empty());

            var result = personService.findAllPaginated(0, 10, "", "", "id", "asc");

            assertNotNull(result);
            assertTrue(result.data().isEmpty());
        }
    }

    @Nested
    class UpdateDetails {
        @BeforeEach
        void setup() {
            testPerson = createMockPerson("", "", "");
        }

        @Test
        @DisplayName("successfully updates details")
        void shouldUpdateDetailsSuccessfully() {
            PersonDetailsUpdateRequest request = new PersonDetailsUpdateRequest(1L, "test name", "newEmail@example.com",
                    "USER", "", false);

            when(personRepository.findById(1L)).thenReturn(Optional.of(testPerson));

            personService.updateDetails(1L, request);

            verify(personRepository).findById(1L);
            verify(personMapper).updatePersonFromRequest(request, testPerson);
            verify(personRepository).save(testPerson);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when person is not found")
        void shouldThrowWhenPersonNotFound() {
            PersonDetailsUpdateRequest request = mock(PersonDetailsUpdateRequest.class);

            when(personRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> personService.updateDetails(1L, request));

            verify(personRepository).findById(1L);
            verifyNoInteractions(personMapper);
        }
    }

    @Nested
    class ChangePassword {
        @BeforeEach
        void setUp() {
            testPerson = createMockPerson("", "", "");

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

    private Person createMockPerson(String role, String email, String password) {
        Person person = new User() {
            @Override
            public String getRole() { return (!role.isEmpty()) ? role : "USER"; }
            @Override
            public Long getId() { return 1L; }
        };
        person.setPassword((!password.isEmpty()) ? password : "encodedCurrentPassword");
        person.setEmail((!email.isEmpty()) ? email : "test@example.com");

        return person;
    }
}
