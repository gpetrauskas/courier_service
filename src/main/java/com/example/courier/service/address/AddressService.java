package com.example.courier.service.address;

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
import com.example.courier.service.auth.AuthService;
import com.example.courier.service.security.CurrentPersonService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
public class AddressService {

    private static final Logger logger = LoggerFactory.getLogger(AddressService.class);

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private AuthService authService;
    @Autowired
    private OrderAddressRepository orderAddressRepository;
    @Autowired
    private CurrentPersonService currentPersonService;

    @Transactional
    public void addressSectionUpdate(AddressSectionUpdateRequest updateRequest) {
        if (currentPersonService.isAdmin()) {
            OrderAddress orderAddress = getOrderAddressById(updateRequest.id());
            addressMapper.updateAddressSectionFromRequest(updateRequest, orderAddress);
            orderAddressRepository.save(orderAddress);
        } else {
            throw new UnauthorizedAccessException("No access.");
        }
    }

    @Transactional(readOnly = true)
    public List<AddressDTO> getAllMyAddresses() {
        try {
            Long userId = currentPersonService.getCurrentPersonId();
            List<Address> addresses = getAddressesByUserId(userId);

            return addresses.stream()
                    .map(addressMapper::toAddressDTO)
                    .toList();
        } catch (EntityNotFoundException e) {
            logger.error("User not found");
            throw new UserNotFoundException("User not found");
        } catch (Exception e) {
            logger.error("Error fetching addresses for user", e);
            throw new RuntimeException("Error fetching addresses", e);
        }
    }

    @Transactional
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        Address address = validateAddressPerson(addressId);
        addressMapper.updateAddressFromDTO(addressDTO, address);
        saveAddress(address);

        logger.info("updated address with id: {}", addressId);

        return addressMapper.toAddressDTO(address);
    }

    @Transactional
    public void deleteAddressById(Long addressId) {
        addressRepository.delete(validateAddressPerson(addressId));
        logger.info("Deleted address with id: {}", addressId);
    }

    public OrderAddress fetchOrCreateOrderAddress(AddressDTO addressDTO, User user) {
        if (addressDTO == null) {
            throw new IllegalArgumentException("AddressDTO cannot be null");
        }
        Address address;
        try {
            if (addressDTO.id() != null) {
                logger.info("Address found with id {}", addressDTO.id());
                address = addressRepository.findByIdAndUserId(addressDTO.id(), user.getId())
                        .orElseThrow(() -> new UserAddressMismatchException("Address not found or not owned by user"));

                if (address.getName() == null || !address.getName().equals(addressDTO.name())) {
                    address.setName(addressDTO.name());
                }
                if (!address.getPhoneNumber().equals(addressDTO.phoneNumber())) {
                    address.setPhoneNumber(addressDTO.phoneNumber());
                }
            } else {
                logger.info("Address not found. Creating new one.");
                address = createNewAddress(addressDTO, user);
                logger.info("New address created. Id: {}", address.getId());
            }
            logger.info("Creating order address from address. ID: {}", address.getId());
            return createOrderAddressFromAddress(address);
        } catch (UserAddressMismatchException ex) {
            throw ex;
        } catch (Exception e) {
            logger.error("Failed to fetch or create OrderAddress for addressDTO {} and user {}", addressDTO, user, e);
            throw new RuntimeException("Failed to fetch or create OrderAddress", e);
        }
    }

    public Address validateAddressPerson(Long addressId) {
        Long personId = currentPersonService.getCurrentPersonId();
        return addressRepository.findByIdAndUserId(addressId, personId)
                .orElseThrow(() -> new AddressNotFoundException("Address was not found using userId: "
                        + personId + " and addressId: " + addressId));
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

    private void saveAddress(Address address) {
        addressRepository.save(address);
        logger.info("Saved address with id {} ", address.getId());
    }

    public List<Address> getAddressesByUserId(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    private OrderAddress createOrderAddressFromAddress(Address address) {
        OrderAddress orderAddress = addressMapper.toOrderAddress(address);
        return orderAddressRepository.saveAndFlush(orderAddress);
    }

    private OrderAddress getOrderAddressById(Long id) {
        return orderAddressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException("No address with such id."));
    }


}
