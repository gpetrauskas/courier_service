/*
package com.example.courier.addressservicetest;

import address.domain.gytis.courier.Address;
import person.domain.gytis.courier.User;
import exception.gytis.courier.AddressNotFoundException;
import com.example.courier.repository.AddressRepository;
import com.example.courier.application.service.address.AddressAppService;
import com.example.courier.adapter.in.security.CurrentPersonUseCase;
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

    @Mock private CurrentPersonUseCase currentPersonService;
    @Mock private AddressRepository addressRepository;

    @InjectMocks private AddressAppService addressService;

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
*/
