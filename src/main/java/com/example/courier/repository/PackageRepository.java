package com.example.courier.repository;

import com.example.courier.common.PackageStatus;
import com.example.courier.domain.Package;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PackageRepository extends JpaRepository<Package, Long> {
    Long countByStatusAndIsAssignedFalse(PackageStatus status);
    Optional<Package> findByTrackingNumber(String trackingNumber);
    List<Package> findByStatus(PackageStatus status);
}
