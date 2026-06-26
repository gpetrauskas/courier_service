package gytis.courier.application.port.out.address;

import gytis.courier.domain.address.Address;

import java.util.Optional;

public interface AddressPersistencePort {
    Optional<Address> findByIdAndUserId(Long addressId, Long userId);
    Address findById(Long addressId);
    Address save(Address address);
    Address create(Address address);
    void deleteById(Long addressId);
    boolean addressIdOwnedByUserId(Long addressId, Long userId);
}
