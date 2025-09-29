package com.example.courier.service.address;

import com.example.courier.domain.Address;
import com.example.courier.domain.AddressDetails;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/** Service responsible for retrieving, creating, updating and deleting addresses.
 *
 * <p>
 *     Implements {@link AddressService} interface and provide transactional operations for managing
 *     user addresses and order addresses.
 * </p>
 */
@Service
public class AddressServiceImpl implements AddressService {

    private static final Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final OrderAddressRepository orderAddressRepository;
    private final CurrentPersonService currentPersonService;


    public AddressServiceImpl(AddressRepository addressRepository, AddressMapper addressMapper,
                          OrderAddressRepository orderAddressRepository, CurrentPersonService currentPersonService) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
        this.orderAddressRepository = orderAddressRepository;
        this.currentPersonService = currentPersonService;
    }

    @Override
    @Transactional
    public void addressSectionUpdate(AddressSectionUpdateRequest updateRequest) {
        OrderAddress orderAddress = orderAddressRepository.findById(updateRequest.id())
                .orElseThrow(() -> new AddressNotFoundException("No address with such id."));

        addressMapper.updateAddressSectionFromRequest(updateRequest, orderAddress);
        orderAddressRepository.save(orderAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDTO> getAllMyAddresses() {
        Long userId = currentPersonService.getCurrentPersonId();
        List<Address> addresses = getAddressesByUserId(userId);

        return addresses.stream()
                .map(addressMapper::toAddressDTO)
                .toList();
    }

    @Override
    @Transactional
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        Address address = findAddressForCurrentPerson(addressId);
        AddressDetails newDetails = addressMapper.toAddressDetails(addressDTO);

        address.updateAddress(newDetails);
        addressRepository.save(address);

        logger.info("updated address with id: {}", addressId);
        return addressMapper.toAddressDTO(address);
    }

    @Override
    @Transactional
    public void deleteAddressById(Long addressId) {
        addressRepository.delete(findAddressForCurrentPerson(addressId));
        logger.info("Deleted address with id: {}", addressId);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderAddress fetchOrCreateOrderAddress(AddressDTO addressDTO, User user) {
        Objects.requireNonNull(addressDTO, "AddressDTO cannot be null");
        Objects.requireNonNull(user, "User cannot be null");

        return (addressDTO.id() != null)
                ? handleExistingAddress(addressDTO, user)
                : createNewAddress(addressDTO, user);
    }

    public Address getAddressById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException("Address not found."));
    }

    /* Helper methods
    */

    private OrderAddress handleExistingAddress(AddressDTO dto, User user) {
        logger.info("Fetching existing address with id {}", dto.id());

        Address address = addressRepository.findByIdAndUserId(dto.id(), user.getId())
                .orElseThrow(() -> new UserAddressMismatchException("Address not found or not owned by user"));

        AddressDetails details = addressMapper.updateFromDTOAndEntity(address, dto);
        return new OrderAddress(details);
    }

    private Address findAddressForCurrentPerson(Long addressId) {
        Long personId = currentPersonService.getCurrentPersonId();
        return addressRepository.findByIdAndUserId(addressId, personId)
                .orElseThrow(() -> new AddressNotFoundException("Address was not found using userId: "
                        + personId + " and addressId: " + addressId));
    }

    @Transactional
    private OrderAddress createNewAddress(AddressDTO addressDTO, User user) {
        AddressDetails details = addressMapper.toAddressDetails(addressDTO);
        Address address = new Address(user, details);
        addressRepository.saveAndFlush(address);
        return addressMapper.toOrderAddress(address);
    }

    private List<Address> getAddressesByUserId(Long userId) {
        return addressRepository.findByUserId(userId);
    }
}
