package com.example.courier.service.address;

import com.example.courier.domain.OrderAddress;
import com.example.courier.domain.User;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.request.order.AddressSectionUpdateRequest;

import java.util.List;

public interface AddressService {
    void addressSectionUpdate(AddressSectionUpdateRequest request);
    List<AddressDTO> getAllMyAddresses();
    AddressDTO updateAddress(Long addressId, AddressDTO dto);
    void deleteAddressById(Long addressId);

    OrderAddress fetchOrCreateOrderAddress(AddressDTO addressDTO, User user);

}
