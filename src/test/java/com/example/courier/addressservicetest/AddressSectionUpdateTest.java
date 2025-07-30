package com.example.courier.addressservicetest;

import com.example.courier.domain.OrderAddress;
import com.example.courier.dto.mapper.AddressMapper;
import com.example.courier.dto.request.order.AddressSectionUpdateRequest;
import com.example.courier.exception.AddressNotFoundException;
import com.example.courier.repository.OrderAddressRepository;
import com.example.courier.service.address.AddressServiceImpl;
import com.example.courier.service.security.CurrentPersonService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressSectionUpdateTest {

    @Mock private CurrentPersonService currentPersonService;
    @Mock private OrderAddressRepository orderAddressRepository;
    @Mock private AddressMapper addressMapper;

    @InjectMocks private AddressServiceImpl addressService;

    @Test
    @DisplayName("should successfully update address section")
    void shouldSuccessfullyUpdateAddressSection() {
        OrderAddress orderAddress = new OrderAddress();
        orderAddress.setId(1L);
        orderAddress.setName("name to be changed");
        orderAddress.setStreet("no name street");

        AddressSectionUpdateRequest request = new AddressSectionUpdateRequest(1L, "addressSection", "gytis", "elm street", "", "", "", "", "");

        when(orderAddressRepository.findById(orderAddress.getId())).thenReturn(Optional.of(orderAddress));
        doAnswer(invocationOnMock -> {
            orderAddress.setName(request.name());
            orderAddress.setStreet(request.street());
            return null;
        }).when(addressMapper).updateAddressSectionFromRequest(request, orderAddress);


        addressService.addressSectionUpdate(request);

        assertEquals("elm street", orderAddress.getStreet());
        assertEquals("gytis", orderAddress.getName());
        verify(orderAddressRepository).findById(anyLong());
        verify(addressMapper).updateAddressSectionFromRequest(request, orderAddress);
        verify(orderAddressRepository).save(orderAddress);
    }

    @Test
    @DisplayName("should fail when address not found")
    void shouldThrowWhenAddressNotFound() {
        when(orderAddressRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class, () -> addressService.addressSectionUpdate(mock(AddressSectionUpdateRequest.class)));
    }
}
