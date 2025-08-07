package com.example.courier.personservice;

import com.example.courier.domain.BanHistory;
import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import com.example.courier.dto.mapper.BanHistoryMapper;
import com.example.courier.dto.request.person.BanActionRequestDTO;
import com.example.courier.dto.response.BanHistoryDTO;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.repository.BanHistoryRepository;
import com.example.courier.service.person.command.BanManagementService;
import com.example.courier.service.person.command.PersonUpdateService;
import com.example.courier.service.person.query.PersonLookupService;
import com.example.courier.service.security.CurrentPersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BanManagementServiceTest {

    @Mock private BanHistoryRepository banHistoryRepository;
    @Mock private CurrentPersonService currentPersonService;
    @Mock private PersonUpdateService updateService;
    @Mock private PersonLookupService lookupService;
    @Mock private BanHistoryMapper mapper;

    @InjectMocks private BanManagementService banManagementService;

    private final Long TEST_PERSON_ID = 1L;
    private Person testPerson;
    private Person testAdmin;

    @BeforeEach
    void setup() {
        testPerson = new User();
        testAdmin = new Person() { @Override public String getRole() {return "ADMIN"; } };
    }

    @Nested
    class BanUnban {
        @Test
        void banUnban_shouldReturnSuccessMessageOnBan() {
            BanActionRequestDTO banActionRequestDTO = new BanActionRequestDTO("test reasons");
            testPerson.setBlocked(false);

            when(lookupService.findNotDeletedPerson(TEST_PERSON_ID)).thenReturn(testPerson);
            when(currentPersonService.getCurrentPerson()).thenReturn(testAdmin);

            var response = banManagementService.banUnban(TEST_PERSON_ID, banActionRequestDTO);

            assertEquals("Person ID 1 was banned successfully", response);
            verify(banHistoryRepository).save(argThat(h ->
                    h.isBanned() && h.getReason().equals("test reasons")));
            verify(updateService).persist(testPerson);
            assertTrue(testPerson.isBlocked());
        }

        @Test
        void banUnban_shouldReturnSuccessMessageOnUnban() {
            BanActionRequestDTO banActionRequestDTO = new BanActionRequestDTO("mistake");
            testPerson.setBlocked(true);

            when(lookupService.findNotDeletedPerson(TEST_PERSON_ID)).thenReturn(testPerson);
            when(currentPersonService.getCurrentPerson()).thenReturn(testAdmin);

            var response = banManagementService.banUnban(TEST_PERSON_ID, banActionRequestDTO);

            assertEquals("Person ID 1 was unbanned successfully", response);
            verify(updateService).persist(testPerson);
            verify(banHistoryRepository).save(argThat(h ->
                    !h.isBanned() && h.getReason().equals("mistake")));
            assertFalse(testPerson.isBlocked());
        }

        @Test
        void banUnban_shouldThrowWHenUserNotFount() {
            BanActionRequestDTO dto = new BanActionRequestDTO("test");

            when(lookupService.findNotDeletedPerson(TEST_PERSON_ID)).thenThrow(ResourceNotFoundException.class);

            assertThrows(ResourceNotFoundException.class, () -> banManagementService.banUnban(TEST_PERSON_ID, dto));
        }

        @Test
        void banUnban_shouldThrowWhenCurrentAdminNotAuthorized() {
            BanActionRequestDTO dto = new BanActionRequestDTO("tests");

            when(lookupService.findNotDeletedPerson(TEST_PERSON_ID)).thenReturn(testPerson);
            when(currentPersonService.getCurrentPerson()).thenThrow(UnauthorizedAccessException.class);

            assertThrows(UnauthorizedAccessException.class, () -> banManagementService.banUnban(TEST_PERSON_ID, dto));
        }
    }

    @Nested
    class GetBanHistory {
        @Test
        void getBanHistory_shouldReturnBanHistoryList() {
            BanHistory banHistory = new BanHistory(testPerson, true, "admin@email.lt", "test reason");
            BanHistoryDTO banHistoryDTO = new BanHistoryDTO(2L, true, "admin@email.lt", "test reason", LocalDateTime.now());

            when(banHistoryRepository.findByPersonIdOrderByActionTimeDesc(TEST_PERSON_ID)).thenReturn(List.of(banHistory));
            when(mapper.toDTO(banHistory)).thenReturn(banHistoryDTO);

            List<BanHistoryDTO> response = banManagementService.getBanHistory(TEST_PERSON_ID);

            assertThat(response)
                    .hasSize(1)
                    .first()
                    .satisfies(dto -> {
                        assertThat(dto.reason()).isEqualTo("test reason");
                        assertThat(dto.actionBy()).isEqualTo("admin@email.lt");
                        assertThat(dto.banned()).isTrue();
                    });
            verify(banHistoryRepository).findByPersonIdOrderByActionTimeDesc(TEST_PERSON_ID);
            verify(mapper).toDTO(banHistory);
        }

        @Test
        void getBanHistory_shouldReturnEmptyListWhenNoHistoryExists() {
            when(banHistoryRepository.findByPersonIdOrderByActionTimeDesc(TEST_PERSON_ID)).thenReturn(List.of());

            List<BanHistoryDTO> response = banManagementService.getBanHistory(TEST_PERSON_ID);

            assertTrue(response.isEmpty());
        }
    }
}
