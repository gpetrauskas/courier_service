package com.example.courier.personserviceimpltest;

import com.example.courier.domain.BanHistory;
import com.example.courier.dto.mapper.BanHistoryMapper;
import com.example.courier.dto.response.BanHistoryDTO;
import com.example.courier.repository.BanHistoryRepository;
import com.example.courier.service.person.PersonServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetBanHistoryTest {
    @Mock private BanHistoryRepository banHistoryRepository;
    @Mock private BanHistoryMapper banHistoryMapper;

    @InjectMocks private PersonServiceImpl personService;

    @Test
    @DisplayName("should fetch ban history list successfully")
    void shouldReturnBanHistoryList() {
        when(banHistoryRepository.findByPersonIdOrderByActionTimeDesc(1L)).thenReturn(List.of(mock(BanHistory.class), mock(BanHistory.class)));
        when(banHistoryMapper.toDTO(any())).thenReturn(any(BanHistoryDTO.class));

        var response = personService.getBanHistory(1L);

        assertEquals(2, response.size());
        verify(banHistoryRepository).findByPersonIdOrderByActionTimeDesc(1L);
        verify(banHistoryMapper, times(2)).toDTO(any());
    }

    @Test
    @DisplayName("should return empty list when no ban history is found")
    void shouldReturnEmptyList() {
        when(banHistoryRepository.findByPersonIdOrderByActionTimeDesc(1L)).thenReturn(List.of());

        var response = personService.getBanHistory(1L);

        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(banHistoryMapper, never()).toDTO(any());
    }
}
