package com.example.courier.personservice;

import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.PersonDetailsUpdateRequest;
import com.example.courier.exception.CompositeValidationException;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.service.person.command.AdminPersonCommandService;
import com.example.courier.service.person.command.PersonUpdateService;
import com.example.courier.service.person.query.PersonLookupService;
import com.example.courier.service.transformation.PersonTransformationService;
import com.example.courier.validation.person.PersonDetailsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminPersonCommandServiceTest {

    @Mock private PersonUpdateService updateService;
    @Mock private PersonLookupService lookupService;
    @Mock private PersonDetailsValidator validator;
    @Mock private PersonTransformationService transformationService;

    @InjectMocks private AdminPersonCommandService adminPersonCommandService;

    private Person testPerson;
    private final Long TEST_PERSON_ID = 1L;

    @BeforeEach
    void setup() {
        testPerson = new User() { public Long getId() { return TEST_PERSON_ID; } };
        testPerson.setName("Old Name");
        testPerson.setEmail("old@email.lt");
        testPerson.setPhoneNumber("37012345678");
    }

    /*
    * Update details
    */
    @Nested
    class UpdateDetails {
        @Test
        void updateDetails_shouldUpdateAllFieldsWhenRequestContainsAllValues() {
            PersonDetailsUpdateRequest request = new PersonDetailsUpdateRequest("New Name", "new@email.lt", "61111111");

            when(lookupService.fetchById(TEST_PERSON_ID)).thenReturn(testPerson);
            when(transformationService.formatPhone("61111111")).thenReturn("37061111111");

            adminPersonCommandService.updateDetails(TEST_PERSON_ID, request);

            assertThat(testPerson)
                    .returns("New Name", Person::getName)
                    .returns("new@email.lt", Person::getEmail)
                    .returns("37061111111", Person::getPhoneNumber);
            verify(updateService).persist(testPerson);
        }

        @Test
        void updateDetails_shouldUpdateOnlyNameWhenOtherFieldsAreNull() {
            PersonDetailsUpdateRequest request = new PersonDetailsUpdateRequest("New Name", null, null);

            when(lookupService.fetchById(TEST_PERSON_ID)).thenReturn(testPerson);

            adminPersonCommandService.updateDetails(TEST_PERSON_ID, request);

            assertThat(testPerson)
                    .returns("New Name", Person::getName)
                    .returns("old@email.lt", Person::getEmail)
                    .returns("37012345678", Person::getPhoneNumber);
            verify(transformationService, never()).formatPhone(anyString());
            verify(updateService).persist(testPerson);
        }

        @Test
        void updateDetails_shouldThrowWhenEmailAlreadyExists() {
            PersonDetailsUpdateRequest request = new PersonDetailsUpdateRequest(null, "old@email.lt", null);

            when(lookupService.checkIfPersonAlreadyExistsByEmail("old@email.lt")).thenReturn(true);

            IllegalArgumentException response = assertThrows(IllegalArgumentException.class,
                    () -> adminPersonCommandService.updateDetails(TEST_PERSON_ID, request));

            assertEquals("Email already used", response.getMessage());
            verify(lookupService, never()).fetchById(anyLong());
        }

        @Test
        void updateDetails_shouldSkipBlankName() {
            PersonDetailsUpdateRequest request = new PersonDetailsUpdateRequest("   ", "new@email.lt", null);

            when(lookupService.checkIfPersonAlreadyExistsByEmail("new@email.lt")).thenReturn(false);
            when(lookupService.fetchById(TEST_PERSON_ID)).thenReturn(testPerson);

            adminPersonCommandService.updateDetails(TEST_PERSON_ID, request);

            assertEquals("Old Name", testPerson.getName());
            verify(updateService).persist(testPerson);
        }

        @Test
        void updateDetails_shouldThrowWithMultipleValidationErrors() {
            PersonDetailsUpdateRequest request = new PersonDetailsUpdateRequest("wron name!!@@!", "nope", "123");

            List<ApiResponseDTO> expectedErrors = List.of(
                    new ApiResponseDTO("name error", "Invalid full name"),
                    new ApiResponseDTO("email error", "Invalid email"),
                    new ApiResponseDTO("phone error", "Invalid phone number")
            );

            doThrow(new CompositeValidationException(expectedErrors)).when(validator).validate(request);

            CompositeValidationException response = assertThrows(CompositeValidationException.class,
                    () -> adminPersonCommandService.updateDetails(TEST_PERSON_ID, request));

            assertEquals(3, response.getErrors().size());
            verify(lookupService, never()).fetchById(anyLong());
            verify(updateService, never()).persist(testPerson);
        }
    }

    /*
    * Soft delete
    */
    @Nested
    class SoftDelete {
        @Test
        void sofDelete_shouldMarkPersonAsDeleted() {
            testPerson.setDeleted(false);

            when(lookupService.fetchById(TEST_PERSON_ID)).thenReturn(testPerson);

            adminPersonCommandService.softDelete(TEST_PERSON_ID);

            assertTrue(testPerson.isDeleted());
            verify(updateService).persist(testPerson);
        }

        @Test
        void softDelete_shouldThrowWhenPersonNotFound() {
            when(lookupService.fetchById(TEST_PERSON_ID)).thenThrow(new ResourceNotFoundException("User not found"));

            assertThrows(ResourceNotFoundException.class, () -> adminPersonCommandService.softDelete(TEST_PERSON_ID));
            verify(updateService, never()).persist(testPerson);
        }

        @Test
        void softDelete_shouldHandleAlreadyDeletedPerson() {
            testPerson.delete();

            when(lookupService.fetchById(TEST_PERSON_ID)).thenReturn(testPerson);

            adminPersonCommandService.softDelete(TEST_PERSON_ID);

            assertTrue(testPerson.isDeleted());
            verify(updateService).persist(testPerson);
        }
    }
}