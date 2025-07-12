package com.example.courier.repository;

import com.example.courier.common.OrderStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.Order;
import com.example.courier.domain.Parcel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    List<Order> findByUserId(Long userId);
    Page<Order> findByUserId(Long userId, Pageable pageable);
    Order findByParcelDetails_TrackingNumber(String trackingNumber);
    Optional<Order> findByParcelDetails(Parcel parcel);
    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

    @Query("SELECT o FROM Order o JOIN o.parcelDetails p WHERE p.status = :status")
    List<Order> findByParcelStatus(@Param("status") ParcelStatus status);

    @Query("SELECT o FROM Order o WHERE o.parcelDetails IN :parcels")
    List<Order> findAllByParcelDetails(@Param("parcels") List<Parcel> parcels);

    int countByUserIdAndStatus(Long userId, OrderStatus status);
}
