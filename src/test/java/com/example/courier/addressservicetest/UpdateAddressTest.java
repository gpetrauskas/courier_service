package com.example.courier.addressservicetest;

import com.example.courier.common.AddressValidationMode;
import com.example.courier.domain.Address;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.mapper.AddressMapper;
import com.example.courier.exception.AddressNotFoundException;
import com.example.courier.exception.CompositeValidationException;
import com.example.courier.repository.AddressRepository;
import com.example.courier.service.address.AddressServiceImpl;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.service.validation.AddressValidationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateAddressTest {
    @Mock private CurrentPersonService currentPersonService;
    @Mock private AddressValidationService validationService;
    @Mock private AddressRepository addressRepository;
    @Mock private AddressMapper addressMapper;

    @InjectMocks private AddressServiceImpl addressService;

    @Test
    void updateAddress_shouldThrowCompositeValidationException_whenFieldsAreInvalid() {
        AddressDTO invalidAddressDTO = new AddressDTO(
                1L,
                "good",
                "@bad@ 4m4t n0 74r0w5",
                "1",
                "Flat11! bad",
                "12345678",
                "12345",
                "good name"
        );

        doThrow(new CompositeValidationException(List.of(
                new ApiResponseDTO("street error", "Invalid street format"),
                new ApiResponseDTO("flat error", "Invalid flat format")
        ))).when(validationService).validateAddress(invalidAddressDTO, AddressValidationMode.UPDATE);


        CompositeValidationException thrown = assertThrows(
                CompositeValidationException.class,
                () -> validationService.validateAddress(invalidAddressDTO, AddressValidationMode.UPDATE)
        );

        assertEquals(2, thrown.getErrors().size());
    }

    @Test
    @DisplayName("successfully updates ser address")
    void shouldUpdateUserAddress() {
        Address address = MockAddressBuilder.dummyAddress(1L, "namer", "rohan");
        AddressDTO dto = MockAddressBuilder.dummyAddressDTO(null, "new name", "gondor");
        AddressDTO updatedAddress = MockAddressBuilder.dummyAddressDTO(1L, "new name", "gondor");

        when(currentPersonService.getCurrentPersonId()).thenReturn(2L);
        when(addressRepository.findByIdAndUserId(1L, 2L)).thenReturn(Optional.of(address));
        doAnswer(invocationOnMock -> {
            address.setName(dto.name());
            address.setCity(dto.city());
            return null;
        }).when(addressMapper).updateAddressFromDTO(dto, address);
        when(addressMapper.toAddressDTO(address)).thenReturn(updatedAddress);

        AddressDTO response = addressService.updateAddress(1L, dto);

        assertNotNull(response);
        assertEquals(address.getName(), response.name());
        assertEquals(address.getCity(), response.city());
        assertEquals(updatedAddress, response);
        verify(addressRepository).save(any());
    }

    @Test
    @DisplayName("should throw AddressNotFoundException when address not found by user id and address id")
    void shouldThrowWhenAddressNotFound() {
        when(currentPersonService.getCurrentPersonId()).thenReturn(1L);
        when(addressRepository.findByIdAndUserId(2L, 1L)).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class, () -> addressService.updateAddress(2L, mock(AddressDTO.class)));
    }


    public static class MockAddressBuilder {
        public static Address dummyAddress(Long id, String name, String city) {
            Address address = new Address();
            address.setId(id);
            address.setName(name);
            address.setCity(city);
            address.setPhoneNumber("12345678");
            address.setStreet("elm");
            address.setPostCode("12345");

            return address;
        }

        public static AddressDTO dummyAddressDTO(Long id, String name, String city) {
            return new AddressDTO(
                    id, city, "", "", "", "", "", name
            );
        }
    }
}
