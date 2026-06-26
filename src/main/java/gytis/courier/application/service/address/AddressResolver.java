package gytis.courier.application.service.address;

import gytis.courier.application.port.out.address.AddressPersistencePort;
import gytis.courier.application.service.order.AddressInput;
import gytis.courier.domain.address.Address;
import gytis.courier.domain.address.AddressDetails;
import gytis.courier.domain.order.OrderAddress;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AddressResolver {
    private final AddressPersistencePort port;

    public AddressResolver(AddressPersistencePort port) {
        this.port = port;
    }

    public OrderAddress resolve(Long id, AddressInput addressInput, Long userId) {
        return (id == null)
                ? createNew(addressInput, userId)
                : fetchExisting(id, userId);
    }

    private OrderAddress createNew(AddressInput input, Long userId) {
        AddressDetails details = AddressDetails.createValidated(
                input.name(),
                input.street(),
                input.houseNumber(),
                input.flatNumber(),
                input.city(),
                input.postCode(),
                input.phoneNumber()
        );

        Address address = port.create(new Address(userId, details));
        return OrderAddress.from(address.getDetails());
    }

    private OrderAddress fetchExisting(Long addressId, Long userId) {
        Address address = port.findByIdAndUserId(addressId, userId).orElseThrow(
                () -> new ResourceNotFoundException("Address not found or not belong to current user"));
        return OrderAddress.from(address.getDetails());
    }
}
