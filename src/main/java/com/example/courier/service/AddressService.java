package com.example.courier.service;

import com.example.courier.domain.Address;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.mapper.AddressMapper;
import com.example.courier.repository.AddressRepository;
import com.example.courier.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    private static final Logger logger = LoggerFactory.getLogger(AddressMapper.class);

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
        addressRepository.save(existingAddress);

        logger.info("updated address with id: {}", id);

        return addressMapper.toAddressDTO(existingAddress);
    }

}
