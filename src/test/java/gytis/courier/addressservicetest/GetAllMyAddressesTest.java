/*
package com.example.courier.addressservicetest;

import address.domain.gytis.courier.Address;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.mapper.AddressMapper;
import exception.gytis.courier.UnauthorizedAccessException;
import com.example.courier.repository.AddressRepository;
import com.example.courier.application.service.address.AddressAppService;
import com.example.courier.adapter.in.security.CurrentPersonUseCase;
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
public class GetAllMyAddressesTest {

    @Mock private CurrentPersonUseCase currentPersonService;
    @Mock private AddressRepository addressRepository;
    @Mock private AddressMapper addressMapper;

    @InjectMocks private AddressAppService addressService;

    @Test
    @DisplayName("successfully return user addresses list")
    void shouldReturnAddressesList() {
        when(currentPersonService.getCurrentPersonId()).thenReturn(1L);
        when(addressRepository.findByUserId(1L)).thenReturn(List.of(mock(Address.class), mock(Address.class)));
        when(addressMapper.toAddressDTO(any(Address.class))).thenReturn(mock(AddressDTO.class));

        var response = addressService.getAllMyAddresses();

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(currentPersonService).getCurrentPersonId();
        verify(addressRepository).findByUserId(anyLong());
        verify(addressMapper, times(2)).toAddressDTO(any());
    }

    @Test
    @DisplayName("should return empty list")
    void shouldReturnEmptyList() {
        when(currentPersonService.getCurrentPersonId()).thenReturn(1L);
        when(addressRepository.findByUserId(1L)).thenReturn(List.of());

        var response = addressService.getAllMyAddresses();

        assertTrue(response.isEmpty());
        verify(addressMapper, never()).toAddressDTO(any());
    }

    @Test
    @DisplayName("should thro Unauthorized when user is not logged in")
    void shouldThrowWhenUserIsNotLoggedIn() {
        when(currentPersonService.getCurrentPersonId()).thenThrow(new UnauthorizedAccessException("Not logged in"));

        assertThrows(UnauthorizedAccessException.class, () -> addressService.getAllMyAddresses());
    }
}
*/
