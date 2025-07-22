package com.example.courier.personserviceimpltest;

import com.example.courier.domain.Person;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.PersonRepository;
import com.example.courier.service.person.PersonServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteTest {

    @Mock private PersonRepository personRepository;

    @InjectMocks private PersonServiceImpl personService;

    private Person person;

    @BeforeEach
    void setup() {
        person = new Person() {
            @Override public String getRole() { return "USER"; }
            @Override public Long getId() { return 1L; }
        };
    }

    @Test
    @DisplayName("successfully deletes person")
    void successfullyDeletePerson() {
        when(personRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(person));

        personService.delete(1L);

        verify(personRepository).save(person);
        assertTrue(person.isDeleted());
    }

    @Test
    @DisplayName("should throw when person not found or already deleted")
    void shouldThrowWhenPersonNotFoundOrAlreadyDeleted() {
        person.setDeleted(true);
        when(personRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> personService.delete(1L));

        verify(personRepository, never()).save(any());
    }
}
