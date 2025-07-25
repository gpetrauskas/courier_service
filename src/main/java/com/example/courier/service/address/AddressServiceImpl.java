package com.example.courier.service.address;

import com.example.courier.domain.Address;
import com.example.courier.domain.OrderAddress;
import com.example.courier.domain.User;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.mapper.AddressMapper;
import com.example.courier.dto.request.order.AddressSectionUpdateRequest;
import com.example.courier.exception.AddressNotFoundException;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.exception.UserAddressMismatchException;
import com.example.courier.repository.AddressRepository;
import com.example.courier.repository.OrderAddressRepository;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.service.validation.AddressValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private static final Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final OrderAddressRepository orderAddressRepository;
    private final CurrentPersonService currentPersonService;
    private final AddressValidationService validationService;


    public AddressServiceImpl(AddressRepository addressRepository, AddressMapper addressMapper,
                          OrderAddressRepository orderAddressRepository, CurrentPersonService currentPersonService,
                              AddressValidationService validationService) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
        this.orderAddressRepository = orderAddressRepository;
        this.currentPersonService = currentPersonService;
        this.validationService = validationService;
    }

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
        Long userId = currentPersonService.getCurrentPersonId();
        List<Address> addresses = getAddressesByUserId(userId);

        return addresses.stream()
                .map(addressMapper::toAddressDTO)
                .toList();
    }

    @Transactional
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        validationService.validateAddress(addressDTO);

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

        try {
            Address address = addressDTO.id() == null
                    ? createNewAddress(addressDTO, user)
                    : handleExistingAddress(addressDTO, user);

            return createOrderAddressFromAddress(address);
        }
        catch (UserAddressMismatchException ex) { throw ex; }
        catch (Exception e) {
            logger.error("Failed to fetch or create OrderAddress for addressDTO {} and user {}", addressDTO, user, e);
            throw new RuntimeException("Failed to fetch or create OrderAddress", e);
        }
    }

    private Address handleExistingAddress(AddressDTO dto, User user) {
        logger.info("Fetching existing address with id {}", dto.id());
        Address address = addressRepository.findByIdAndUserId(dto.id(), user.getId())
                .orElseThrow(() -> new UserAddressMismatchException("Address not found or not owned by user"));
        updateNameAndPhoneIfChanged(address, dto);
        return address;
    }

    private void updateNameAndPhoneIfChanged(Address address, AddressDTO dto) {
        if (address.getName() == null || !address.getName().equals(dto.name())) {
            address.setName(dto.name());
        }
        if (address.getPhoneNumber() == null || !address.getPhoneNumber().equals(dto.phoneNumber())) {
            address.setPhoneNumber(dto.phoneNumber());
        }
    }

    private Address validateAddressPerson(Long addressId) {
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

    private List<Address> getAddressesByUserId(Long userId) {
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
