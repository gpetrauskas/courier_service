package com.example.courier.repository;

import com.example.courier.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    Order findByTrackingNumber(String trackingNumber);
}
