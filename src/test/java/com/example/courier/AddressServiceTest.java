package com.example.courier;

import com.example.courier.domain.Address;
import com.example.courier.domain.OrderAddress;
import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.mapper.AddressMapper;
import com.example.courier.dto.request.order.AddressSectionUpdateRequest;
import com.example.courier.exception.AddressNotFoundException;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.exception.UserAddressMismatchException;
import com.example.courier.exception.UserNotFoundException;
import com.example.courier.repository.AddressRepository;
import com.example.courier.repository.OrderAddressRepository;
import com.example.courier.service.address.AddressService;
import com.example.courier.service.security.CurrentPersonService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {
    @Mock
    private CurrentPersonService currentPersonService;
    @Mock
    private AddressMapper addressMapper;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private OrderAddressRepository orderAddressRepository;

    private final AddressSectionUpdateRequest request = new AddressSectionUpdateRequest(1L, "addressSection", "me", null, null, null, null, null, null);
    private final static Long USER_ID = 1L;

    @InjectMocks
    private AddressService addressService;

    @BeforeEach
    void setUp() {
        lenient().when(currentPersonService.getCurrentPersonId()).thenReturn(USER_ID);
    }

    @Nested
    class AddressSectionUpdate {
        @Test
        @DisplayName("success")
        void success() {
            OrderAddress existingAddress = new OrderAddress();
            existingAddress.setId(1L);
            existingAddress.setName("test");

            when(currentPersonService.isAdmin()).thenReturn(true);
            when(orderAddressRepository.findById(1L)).thenReturn(Optional.of(existingAddress));
            doAnswer(invocationOnMock -> {
                    ((OrderAddress)invocationOnMock.getArgument(1)).setName(request.name());
                    return null;
            }).when(addressMapper).updateAddressSectionFromRequest(request, existingAddress);

            addressService.addressSectionUpdate(request);

            verify(orderAddressRepository).findById(1L);
            verify(addressMapper).updateAddressSectionFromRequest(any(), any());
            assertEquals("me", existingAddress.getName());
        }

        @Test
        @DisplayName("address not found")
        void orderAddressNotFound_failure() {
            when(currentPersonService.isAdmin()).thenReturn(true);
            when(orderAddressRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(AddressNotFoundException.class, () ->
                    addressService.addressSectionUpdate(request));
        }

        @Test
        @DisplayName("no access")
        void orderAddressNoAccess_failure() {
            when(currentPersonService.isAdmin()).thenReturn(false);

            assertThrows(UnauthorizedAccessException.class, () ->
                    addressService.addressSectionUpdate(request));
        }
    }

    @Nested
    class GetAllMyAddresses {
        @Test
        @DisplayName("get all saved user addresses")
        void success() {
            Address address = TestAddressBuilder.dummyAddress(99L, "home");
            Address address1 = TestAddressBuilder.dummyAddress(98L, "office");

            AddressDTO dto = TestAddressBuilder.dummyAddressDTO(address.getId(), address.getName());
            AddressDTO dto1 = TestAddressBuilder.dummyAddressDTO(address1.getId(), address1.getName());

            List<Address> addresses = List.of(address, address1);

            when(addressRepository.findByUserId(1L)).thenReturn(addresses);
            when(addressMapper.toAddressDTO(address)).thenReturn(dto);
            when(addressMapper.toAddressDTO(address1)).thenReturn(dto1);

            List<AddressDTO> dtoList = addressService.getAllMyAddresses();

            verify(currentPersonService).getCurrentPersonId();
            verify(addressRepository).findByUserId(anyLong());
            verify(addressMapper, times(2)).toAddressDTO(any(Address.class));

            assertEquals(2, dtoList.size());
            assertEquals("home", dtoList.get(0).name());
            assertEquals("office", dtoList.get(1).name());
        }

        @Test
        @DisplayName("get empty address list")
        void emptyList() {
            when(addressRepository.findByUserId(1L)).thenReturn(List.of());

            List<AddressDTO> dtoList = addressService.getAllMyAddresses();

            verify(addressMapper, never()).toAddressDTO(any(Address.class));
            verify(currentPersonService).getCurrentPersonId();
            verify(addressRepository).findByUserId(1L);

            assertEquals(0, dtoList.size());
        }

        @Test
        @DisplayName("fails - user not found")
        void userNotFound_failure() {
            when(currentPersonService.getCurrentPersonId()).thenThrow(EntityNotFoundException.class);

            UserNotFoundException ex = assertThrows(UserNotFoundException.class, () ->
                    addressService.getAllMyAddresses());

            assertEquals("User not found", ex.getMessage());
        }

        @Test
        @DisplayName("fails - generic error")
        void genericFailure() {
            when(currentPersonService.getCurrentPersonId()).thenReturn(1L);
            when(addressRepository.findByUserId(1L)).thenThrow(new RuntimeException("fb error"));

            RuntimeException ex = assertThrows(RuntimeException.class, () ->
                    addressService.getAllMyAddresses());

            assertEquals("Error fetching addresses", ex.getMessage());
        }
    }

    @Nested
    class UpdateAddress {
        @Test
        @DisplayName("successfully update address")
        void success() {
            Address addressOld = TestAddressBuilder.dummyAddress(99L, "old-name");
            Address addressNew = TestAddressBuilder.dummyAddress(99L, "new-name");
            AddressDTO dto = TestAddressBuilder.dummyAddressDTO(addressNew.getId(), addressNew.getName());
            AddressDTO updatedDto = TestAddressBuilder.dummyAddressDTO(addressNew.getId(), addressNew.getName());

            when(addressRepository.findByIdAndUserId(addressOld.getId(), 1L)).thenReturn(Optional.of(addressOld));
            doAnswer(invocationOnMock -> {
                Address address1 = invocationOnMock.getArgument(1);
                AddressDTO dto1 = invocationOnMock.getArgument(0);
                address1.setName(dto1.name());
                return null;
            }).when(addressMapper).updateAddressFromDTO(dto, addressOld);
            when(addressMapper.toAddressDTO(addressOld)).thenReturn(updatedDto);

            AddressDTO updatedDTO = addressService.updateAddress(99L, dto);

            assertEquals("new-name", updatedDTO.name());
            verify(addressMapper).updateAddressFromDTO(any(AddressDTO.class), any(Address.class));
            verify(addressMapper).toAddressDTO(any(Address.class));
            verify(addressRepository).save(any(Address.class));
        }

        @Test
        @DisplayName("address not found")
        void addressNotFound_failure() {
            Address address = TestAddressBuilder.dummyAddress(99L, "fake");
            AddressDTO dto = TestAddressBuilder.dummyAddressDTO(address.getId(), address.getName());

            when(addressRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

            AddressNotFoundException ex = assertThrows(AddressNotFoundException.class, () ->
                    addressService.updateAddress(99L, dto));

            assertEquals("Address was not found using userId: " + 1 + " and addressId: " + 99, ex.getMessage());
            verify(currentPersonService).getCurrentPersonId();
            verify(addressMapper, never()).toAddressDTO(any(Address.class));
            verify(addressMapper, never()).updateAddressFromDTO(any(AddressDTO.class), any(Address.class));
            verify(addressRepository, never()).save(any(Address.class));
        }


    }

    @Nested
    class DeleteAddress {
        @Test
        @DisplayName("delete successfully")
        void success() {
            Address address = TestAddressBuilder.dummyAddress(99L, "test");

            when(addressRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.of(address));

            addressService.deleteAddressById(99L);

            verify(addressRepository).delete(address);
        }

        @Test
        @DisplayName("address not found")
        void addressNotFound() {
            when(addressRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

            AddressNotFoundException ex = assertThrows(AddressNotFoundException.class, () ->
                    addressService.deleteAddressById(99L));

            assertEquals("Address was not found using userId: " + 1L + " and addressId: " + 99L, ex.getMessage());
        }

        @Test
        @DisplayName("cannot authenticate user")
        void userNotAuthenticated() {
            when(currentPersonService.getCurrentPersonId()).thenThrow(new UnauthorizedAccessException("Not logged in"));

            UnauthorizedAccessException ex = assertThrows(UnauthorizedAccessException.class, () ->
                    addressService.deleteAddressById(99L));

            assertEquals("Not logged in", ex.getMessage());
        }
    }

    @Nested
    class FetchOrCreateOrderAddress {
        @Test
        @DisplayName("throws IllegalArgumentException when AdressDTO is null")
        void nullAddressDTO_throws() {
            User user = new User();
            assertThrows(IllegalArgumentException.class, () ->
                    addressService.fetchOrCreateOrderAddress(null, user));
        }

        @Test
        @DisplayName("throws when address not found")
        void addressNotFound_throws() throws NoSuchFieldException, IllegalAccessException {
            Address address = TestAddressBuilder.dummyAddress(99L, "test");
            AddressDTO dto = TestAddressBuilder.dummyAddressDTO(address.getId(), address.getName());

            User user = new User();
            Field field = Person.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, 1L);

            when(addressRepository.findByIdAndUserId(99L, 1L)).thenThrow(UserAddressMismatchException.class);

            assertThrows(UserAddressMismatchException.class, () -> addressService.fetchOrCreateOrderAddress(dto, user));
        }

        @Test
        @DisplayName("success with null id")
        void success_withNullId() {
            Address addressJustForDTO = TestAddressBuilder.dummyAddress(null, "tester");
            AddressDTO dtoWithNullId = TestAddressBuilder.dummyAddressDTO(addressJustForDTO.getId(), addressJustForDTO.getName());
            User user = new User();

            Address mockedAddress = TestAddressBuilder.dummyAddress(1L, "aha");

            when(addressMapper.toAddress(dtoWithNullId)).thenReturn(mockedAddress);
            when(addressRepository.saveAndFlush(any(Address.class))).thenReturn(mockedAddress);

            OrderAddress orderAddress = new OrderAddress();
            orderAddress.setId(1L);
            orderAddress.setName("tester");
            when(addressMapper.toOrderAddress(mockedAddress)).thenReturn(orderAddress);
            when(orderAddressRepository.saveAndFlush(any(OrderAddress.class))).thenReturn(orderAddress);

            OrderAddress orderAddress1 = addressService.fetchOrCreateOrderAddress(dtoWithNullId, user);

            verify(addressRepository).saveAndFlush(any(Address.class));
            verify(addressMapper).toAddress(any(AddressDTO.class));
            assertEquals("tester", orderAddress1.getName());
        }

        @Test
        @DisplayName("success with id")
        void success_withId() throws NoSuchFieldException, IllegalAccessException {
            Address address = TestAddressBuilder.dummyAddress(99L, "new-name");
            AddressDTO dto = TestAddressBuilder.dummyAddressDTO(address.getId(), address.getName());

            Address existentAddress = TestAddressBuilder.dummyAddress(98L, "old-name");

            User user = new User();
            Field idField = Person.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, 1L);

            when(addressRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.of(existentAddress));

            OrderAddress orderAddress = new OrderAddress();
            orderAddress.setId(1L);
            orderAddress.setName("new-name");
            when(addressMapper.toOrderAddress(existentAddress)).thenReturn(orderAddress);
            when(orderAddressRepository.saveAndFlush(any(OrderAddress.class))).thenReturn(orderAddress);

            OrderAddress orderAddress1 = addressService.fetchOrCreateOrderAddress(dto, user);

            assertEquals("new-name", orderAddress1.getName());
            verify(addressMapper, never()).toAddress(any(AddressDTO.class));
            verify(addressMapper).toOrderAddress(any(Address.class));
        }
    }

    public static class TestAddressBuilder {
        public static AddressDTO dummyAddressDTO(Long id, String name) {
            return new AddressDTO(id, "city", "street", "123", "321",
                    "390123456", "9999", name);
        }

        public static Address dummyAddress(Long id, String name) {
            Address address = new Address();
            address.setId(id);
            address.setName(name);
            address.setCity("city");
            address.setFlatNumber("1");
            address.setHouseNumber("22");
            address.setPhoneNumber("370000000");
            address.setPostCode("12345");
            address.setStreet("street");

            return address;
        }
    }
}
