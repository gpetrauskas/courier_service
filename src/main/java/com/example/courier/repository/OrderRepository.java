package com.example.courier.repository;

import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.Order;
import com.example.courier.domain.Parcel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    List<Order> findByUserId(Long userId);
    Order findByParcelDetails_TrackingNumber(String trackingNumber);
    Optional<Order> findByParcelDetails(Parcel parcel);

    @Query("SELECT o FROM Order o JOIN o.parcelDetails p WHERE p.status = :status")
    List<Order> findByParcelStatus(@Param("status") ParcelStatus status);

}
