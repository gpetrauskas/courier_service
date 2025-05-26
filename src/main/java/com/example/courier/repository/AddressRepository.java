package com.example.courier.repository;

import com.example.courier.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long id);
    Optional<Address> findByIdAndUserId(Long addressId, Long userId);
}
