package com.example.courier.addressservicetest;

import com.example.courier.domain.Address;
import com.example.courier.domain.User;
import com.example.courier.exception.AddressNotFoundException;
import com.example.courier.repository.AddressRepository;
import com.example.courier.service.address.AddressServiceImpl;
import com.example.courier.service.security.CurrentPersonService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteAddressTest {

    @Mock private CurrentPersonService currentPersonService;
    @Mock private AddressRepository addressRepository;

    @InjectMocks private AddressServiceImpl addressService;

    @Test
    @DisplayName("successfully deletes address")
    void shouldSuccessfullyDeleteAddress() {
        Address address = new Address();
        address.setId(2L);
        User user = new User();
        user.setAddresses(new ArrayList<>(List.of(address)));

        when(currentPersonService.getCurrentPersonId()).thenReturn(1L);
        when(addressRepository.findByIdAndUserId(2L, 1L)).thenReturn(Optional.of(address));

        addressService.deleteAddressById(2L);
        user.getAddresses().remove(address);

        assertTrue(user.getAddressById(2L).isEmpty());
        verify(addressRepository).delete(address);
    }

    @Test
    @DisplayName("should throw AddressNotFoundException when address not exist or doesnt belong to current user")
    void shouldThrowWhenAddressDoesNotBelongToCurrentUserOrNotExists() {
        when(currentPersonService.getCurrentPersonId()).thenReturn(1L);
        when(addressRepository.findByIdAndUserId(2L, 1L)).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class, () -> addressService.deleteAddressById(2L));
    }
}
