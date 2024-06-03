package com.example.courier.service;

import com.example.courier.domain.Address;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.mapper.AddressMapper;
import com.example.courier.repository.AddressRepository;
import com.example.courier.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressMapper addressMapper;

    public List<Address> getAllMyAddresses(String email) {
        Long userId = userRepository.findByEmail(email).getId();
        return addressRepository.findByUserId(userId);
    }

    @Transactional
    public AddressDTO updateAddress(Long id, AddressDTO addressDTO) {
        Address existingAddress = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found."));
        addressMapper.updateAddressFromDTO(addressDTO, existingAddress);
        Address updatedAddress = addressRepository.save(existingAddress);

        return addressMapper.toAddressDTO(updatedAddress);
    }

}
