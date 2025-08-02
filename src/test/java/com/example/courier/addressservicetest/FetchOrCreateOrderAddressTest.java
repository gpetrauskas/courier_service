package com.example.courier.addressservicetest;

import com.example.courier.common.AddressValidationMode;
import com.example.courier.domain.Address;
import com.example.courier.domain.OrderAddress;
import com.example.courier.domain.User;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.mapper.AddressMapper;
import com.example.courier.exception.UserAddressMismatchException;
import com.example.courier.repository.AddressRepository;
import com.example.courier.repository.OrderAddressRepository;
import com.example.courier.service.address.AddressServiceImpl;
import com.example.courier.service.validation.AddressValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FetchOrCreateOrderAddressTest {

    @Mock private AddressMapper addressMapper;
    @Mock private AddressRepository addressRepository;
    @Mock private AddressValidationService addressValidationService;
    @Mock private OrderAddressRepository orderAddressRepository;

    @InjectMocks private AddressServiceImpl addressService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User() { public Long getId() { return 2L; } } ;
    }

    @Test
    @DisplayName("successfully creates new address")
    void shouldCreateAddressForCreateNewMode() {
        AddressDTO dto = createAddressDTO(null);
        Address address = createMockAddress(dto);
        OrderAddress orderAddress = createMockOrderAddress(dto);

        when(addressMapper.toAddress(dto)).thenReturn(address);
        when(addressMapper.toOrderAddress(address)).thenReturn(orderAddress);
        when(addressRepository.saveAndFlush(address)).thenReturn(address);
        when(orderAddressRepository.saveAndFlush(orderAddress)).thenReturn(orderAddress);

        var response = addressService.fetchOrCreateOrderAddress(dto, user);

        assertEquals(orderAddress, response);
        verify(addressMapper).toAddress(dto);
        verify(addressMapper).toOrderAddress(address);
        verify(addressRepository).saveAndFlush(address);
        verify(orderAddressRepository).saveAndFlush(orderAddress);
        verify(addressValidationService).validateAddress(dto, AddressValidationMode.CREATE_NEW);
    }

    @Test
    @DisplayName("should use USE_EXISTING mode then addressdto has id")
    void shouldUseUpdateModeForDTOWithId() {
        AddressDTO dto = createAddressDTO(1L);

        when(addressRepository.findByIdAndUserId(1L, 2L)).thenReturn(Optional.of(new Address()));

        addressService.fetchOrCreateOrderAddress(dto, user);

        verify(addressValidationService).validateAddress(dto, AddressValidationMode.USE_EXISTING);
    }

    @Test
    @DisplayName("should throw NullPointerException when addressDTo null")
    void shouldThrowWhenAddressDTOIsNull() {
        NullPointerException response = assertThrows(NullPointerException.class, () ->
                addressService.fetchOrCreateOrderAddress(null, mock(User.class)));

        assertEquals("AddressDTO cannot be null", response.getMessage());
    }

    @Test
    @DisplayName("should throw NullPointerException when user is null")
    void shouldThrowWhenUserIsNull() {
        NullPointerException response = assertThrows(NullPointerException.class, () ->
                addressService.fetchOrCreateOrderAddress(mock(AddressDTO.class), null));

        assertEquals("User cannot be null", response.getMessage());

    }

    @Test
    @DisplayName("should throw UserAddressMismatchException when address doesnt belong to current user")
    void shouldThrowWhenAddressNotBelongToCurrentUser() {
        AddressDTO dto = createAddressDTO(2L);

        when(addressRepository.findByIdAndUserId(2L, 2L)).thenReturn(Optional.empty());

        assertThrows(UserAddressMismatchException.class, () -> addressService.fetchOrCreateOrderAddress(dto, user));
    }

    private Address createMockAddress(AddressDTO dto) {
        Address address = new Address();
        address.setName(dto.name());
        address.setPhoneNumber(dto.phoneNumber());
        address.setCity(dto.city());
        address.setStreet(dto.street());
        address.setPostCode(dto.postCode());
        address.setHouseNumber(dto.houseNumber());
        address.setFlatNumber(dto.flatNumber());

        return address;
    }

    private OrderAddress createMockOrderAddress(AddressDTO dto) {
        OrderAddress orderAddress = new OrderAddress();
        orderAddress.setName(dto.name());
        orderAddress.setPhoneNumber(dto.phoneNumber());
        orderAddress.setCity(dto.city());
        orderAddress.setStreet(dto.street());
        orderAddress.setPostCode(dto.postCode());
        orderAddress.setHouseNumber(dto.houseNumber());
        orderAddress.setFlatNumber(dto.flatNumber());
        return orderAddress;
    }

    private AddressDTO createAddressDTO(Long id) {
        return new AddressDTO(id, "springwood", "elm street", "1428", "", "12345678", "12345", "freddy");
    }
}
