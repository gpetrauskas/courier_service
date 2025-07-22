package com.example.courier.personserviceimpltest;

import com.example.courier.domain.Admin;
import com.example.courier.domain.BanHistory;
import com.example.courier.domain.Person;
import com.example.courier.dto.request.person.BanActionRequestDTO;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.BanHistoryRepository;
import com.example.courier.repository.PersonRepository;
import com.example.courier.service.person.PersonServiceImpl;
import com.example.courier.service.security.CurrentPersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BanActionTest {

    @Mock private PersonRepository personRepository;
    @Mock private CurrentPersonService currentPersonService;
    @Mock private BanHistoryRepository banHistoryRepository;

    @InjectMocks private PersonServiceImpl personService;

    private Person person;
    private final Person adminMock = mock(Admin.class);

    @BeforeEach
    void setup() {
        person = new Person() {
            @Override public String getRole() { return "USER"; }
            @Override public Long getId() { return 1L; }
            @Override public String getEmail() { return "user@email.lt" ;}
        };
    }

    @ParameterizedTest(name = "when blocked={0}, should end up blocked={1} with reason={2}")
    @CsvSource({
            "false, true, too cool",
            "true, false, fine"
    })
    void toggleBan_shouldUpdateBlockedStateAndLogHistoryCorrectly(boolean initialState, boolean expectedState, String reason) {
        BanActionRequestDTO requestDTO = new BanActionRequestDTO(reason);

        when(personRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(person));
        when(currentPersonService.getCurrentPerson()).thenReturn(adminMock);
        when(adminMock.getEmail()).thenReturn("admin@email.lt");

        person.setBlocked(initialState);
        personService.banUnban(1L, requestDTO);

        ArgumentCaptor<BanHistory> captor = ArgumentCaptor.forClass(BanHistory.class);
        verify(banHistoryRepository).save(captor.capture());

        BanHistory savedHistory = captor.getValue();

        assertEquals(reason, savedHistory.getReason());
        assertEquals("admin@email.lt", savedHistory.getActionBy());
        assertEquals(person, savedHistory.getPerson());
        assertEquals(expectedState, person.isBlocked());
        verify(personRepository).save(person);
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when person is not found or is marked as deleted")
    void shouldThrowPersonNotFound() {
        when(personRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> personService.banUnban(1L, mock(BanActionRequestDTO.class)));
    }
}
