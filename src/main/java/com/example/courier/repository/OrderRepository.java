package com.example.courier.repository;

import com.example.courier.common.PackageStatus;
import com.example.courier.domain.Order;
import com.example.courier.domain.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    List<Order> findByUserId(Long userId);
    Order findByPackageDetails_TrackingNumber(String trackingNumber);
    Optional<Order> findByPackageDetails(Package parcel);

    @Query("SELECT o FROM Order o JOIN o.packageDetails p WHERE p.status = :status")
    List<Order> findByPackageStatus(@Param("status") PackageStatus status);

}
