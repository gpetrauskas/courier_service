package com.example.courier.service;

import com.example.courier.domain.Address;
import com.example.courier.domain.User;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.mapper.AddressMapper;
import com.example.courier.exception.AddressNotFoundException;
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
        Address existingAddress = getAddressById(id);

        addressMapper.updateAddressFromDTO(addressDTO, existingAddress);
        saveAddress(existingAddress);

        logger.info("updated address with id: {}", id);

        return addressMapper.toAddressDTO(existingAddress);
    }

    public Address getAddress(AddressDTO addressDTO, User user) {
        if (addressDTO.id() != null) {
            Address address = getAddressById(addressDTO.id());
            validateAddressUser(address, user);
            return address;
        }

        return createNewAddress(addressDTO, user);
    }

    private void validateAddressUser(Address address, User user) {
        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Address does not belong to the user.");
        }
    }

    @Transactional
    private Address createNewAddress(AddressDTO addressDTO, User user) {
        Address address = addressMapper.toAddress(addressDTO);
        address.setUser(user);
        return addressRepository.saveAndFlush(address);
    }

    public Address getAddressById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException("Address not found."));
    }

    public void saveAddress(Address address) {
        addressRepository.save(address);
    }
}
