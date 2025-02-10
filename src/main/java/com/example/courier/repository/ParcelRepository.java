package com.example.courier.repository;

import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.Parcel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParcelRepository extends JpaRepository<Parcel, Long> {
    Long countByStatusAndIsAssignedFalse(ParcelStatus status);
    Optional<Parcel> findByTrackingNumber(String trackingNumber);
    List<Parcel> findByStatus(ParcelStatus status);
}
