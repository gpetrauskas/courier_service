package com.example.courier.repository;

import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.Parcel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParcelRepository extends JpaRepository<Parcel, Long> {
    Long countByStatusAndIsAssignedFalse(ParcelStatus status);
    Optional<Parcel> findByTrackingNumber(String trackingNumber);
    List<Parcel> findByStatus(ParcelStatus status);

    @Query("SELECT p.status FROM Parcel p WHERE p.trackingNumber = :trackingNumber")
    Optional<ParcelStatus> findStatusByTrackingNumber(@Param("trackingNumber") String trackingNumber);
}
