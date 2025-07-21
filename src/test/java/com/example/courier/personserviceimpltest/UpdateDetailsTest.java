package com.example.courier.personserviceimpltest;

import com.example.courier.domain.Person;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.PersonDetailsUpdateRequest;
import com.example.courier.exception.CompositeValidationException;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.PersonRepository;
import com.example.courier.service.person.PersonServiceImpl;
import com.example.courier.validation.person.EmailValidator;
import com.example.courier.validation.person.NameValidator;
import com.example.courier.validation.person.PersonDetailsValidator;
import com.example.courier.validation.person.PhoneValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateDetailsTest {

    @Mock private PersonDetailsValidator personDetailsValidator;
    @Mock private PersonRepository personRepository;
    @Mock private EmailValidator emailValidator;
    @Mock private PhoneValidator phoneValidator;
    @Mock private NameValidator nameValidator;

    private Person person;

    @InjectMocks
    private PersonServiceImpl personService;

    @BeforeEach
    void setup() {
        person = createPerson("origin", "old@email.lt", "37000000000");
    }

    @Nested
    @DisplayName("updateDetails() success cases")
    class UpdateDetailsSuccess {

        @Test
        @DisplayName("should update all fields when valid")
        void shouldUpdateAllFieldsWhenValid() {
            var request = new PersonDetailsUpdateRequest("name name", "new@email.lt", "12345678");

            mockValidators(true, true, true);
            when(phoneValidator.format(request.phoneNumber())).thenReturn("370" + request.phoneNumber());
            mockRepo(1L, person);

            personService.updateDetails(1L, request);

            assertEquals(request.email(), person.getEmail());
            assertEquals("370" + request.phoneNumber(), person.getPhoneNumber());
            assertEquals(request.name(), person.getName());
            verify(personDetailsValidator).validate(request);
            verify(personRepository).save(person);
        }

        @Test
        @DisplayName("should update only non blank fields")
        void shouldSkipBlankPhoneField() {
            var request = new PersonDetailsUpdateRequest("name name", "new@email.lt", "");

            mockRepo(1L, person);
            mockValidators(true, true, false);

            System.out.println("Initial phone: " + person.getPhoneNumber());


            personService.updateDetails(1L, request);

            assertEquals(person.getName(), request.name());
            assertEquals(person.getEmail(), request.email());
            assertEquals("37000000000", person.getPhoneNumber());
            verify(personRepository).save(person);
        }
    }

    @Nested
    @DisplayName("updateDetails() failure cases")
    class UpdateDetailsFailure {
        @Test
        @DisplayName("should throw and return all errors for bad inputs")
        void shouldThrowValidationErrorAndSkipUpdateBeforePersonFetch() {
            var request = new PersonDetailsUpdateRequest("name fine", "bad", "123");

            doThrow(new CompositeValidationException(List.of(
                    new ApiResponseDTO("email error", "Invalid email"),
                    new ApiResponseDTO("phone error", "invalid phone format")
            ))).when(personDetailsValidator).validate(request);

            var ex = assertThrows(CompositeValidationException.class, () ->
                    personService.updateDetails(person.getId(), request));

            assertEquals(2, ex.getErrors().size());
            assertEquals("origin", person.getName());
            verify(personRepository, never()).save(person);
        }

        @Test
        @DisplayName("should throw user not found")
        void userNotFoundShouldThrow() {
            var request = new PersonDetailsUpdateRequest("name", "email@domain.com", "12345678");

            when(personRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> personService.updateDetails(99L, request));
        }
    }

    private void mockValidators(boolean nameOk, boolean emailOk, boolean phoneOk) {
        when(nameValidator.isValid(anyString())).thenReturn(nameOk);
        when(emailValidator.isValid(anyString())).thenReturn(emailOk);
        when(phoneValidator.isValid(anyString())).thenReturn(phoneOk);
    }

    private void mockRepo(Long id, Person person) {
        when(personRepository.findById(id)).thenReturn(Optional.of(person));
    }

    private void verifySave() {
        verify(personRepository).save(person);
    }

    private static Person createPerson(String name, String email, String phoneNumber) {
        Person person = new Person() {
            @Override public String getRole() { return "USER"; }
            @Override public Long getId() { return 1L; }
        };
        person.setName(name);
        person.setEmail(email);
        person.setPhoneNumber(phoneNumber);

        return person;
    }
}
