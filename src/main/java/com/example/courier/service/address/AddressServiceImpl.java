package com.example.courier.service.address;

import com.example.courier.common.AddressValidationMode;
import com.example.courier.domain.Address;
import com.example.courier.domain.OrderAddress;
import com.example.courier.domain.User;
import com.example.courier.dto.AddressDTO;
import com.example.courier.dto.mapper.AddressMapper;
import com.example.courier.dto.request.order.AddressSectionUpdateRequest;
import com.example.courier.exception.AddressNotFoundException;
import com.example.courier.exception.UserAddressMismatchException;
import com.example.courier.repository.AddressRepository;
import com.example.courier.repository.OrderAddressRepository;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.service.validation.AddressValidationService;
import com.example.courier.validation.person.PhoneValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class AddressServiceImpl implements AddressService {

    private static final Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final OrderAddressRepository orderAddressRepository;
    private final CurrentPersonService currentPersonService;
    private final AddressValidationService validationService;
    private final PhoneValidator phoneValidator;


    public AddressServiceImpl(AddressRepository addressRepository, AddressMapper addressMapper,
                          OrderAddressRepository orderAddressRepository, CurrentPersonService currentPersonService,
                              AddressValidationService validationService, PhoneValidator phoneValidator) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
        this.orderAddressRepository = orderAddressRepository;
        this.currentPersonService = currentPersonService;
        this.validationService = validationService;
        this.phoneValidator = phoneValidator;
    }

    @Transactional
    public void addressSectionUpdate(AddressSectionUpdateRequest updateRequest) {
        OrderAddress orderAddress = getOrderAddressById(updateRequest.id());
        addressMapper.updateAddressSectionFromRequest(updateRequest, orderAddress);
        orderAddressRepository.save(orderAddress);
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
        validationService.validateAddress(addressDTO, AddressValidationMode.UPDATE);

        Address address = findAddressForCurrentPerson(addressId);
        addressMapper.updateAddressFromDTO(addressDTO, address);
        saveAddress(address);

        logger.info("updated address with id: {}", addressId);

        return addressMapper.toAddressDTO(address);
    }

    @Transactional
    public void deleteAddressById(Long addressId) {
        addressRepository.delete(findAddressForCurrentPerson(addressId));
        logger.info("Deleted address with id: {}", addressId);
    }

    public OrderAddress fetchOrCreateOrderAddress(AddressDTO addressDTO, User user) {
        Objects.requireNonNull(addressDTO, "AddressDTO cannot be null");
        Objects.requireNonNull(user, "User cannot be null");

        AddressValidationMode mode = determineValidationMode(addressDTO);
        validationService.validateAddress(addressDTO, mode);

        Address address = processAddressByMode(addressDTO, user, mode);
        return createOrderAddressFromAddress(address);
    }

    private Address handleExistingAddress(AddressDTO dto, User user) {
        logger.info("Fetching existing address with id {}", dto.id());

        Address address = addressRepository.findByIdAndUserId(dto.id(), user.getId())
                .orElseThrow(() -> new UserAddressMismatchException("Address not found or not owned by user"));

        updateRecipientDetailsIfChanged(address, dto);
        return address;
    }

    private void updateRecipientDetailsIfChanged(Address address, AddressDTO dto) {
        addressMapper.updateNameAndPhoneOnly(dto, address, phoneValidator);
    }

    private Address findAddressForCurrentPerson(Long addressId) {
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

    private AddressValidationMode determineValidationMode(AddressDTO dto) {
        return dto.id() == null
                ? AddressValidationMode.CREATE_NEW
                : AddressValidationMode.USE_EXISTING;
    }

    private Address processAddressByMode(AddressDTO addressDTO, User user, AddressValidationMode mode) {
        return switch (mode) {
            case CREATE_NEW -> createNewAddress(addressDTO, user);
            case USE_EXISTING -> handleExistingAddress(addressDTO, user);
            default -> throw new IllegalArgumentException("Unsuported mode: " + mode);
        };
    }
}
