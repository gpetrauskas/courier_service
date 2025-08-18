package com.example.courier.repository;

import com.example.courier.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);
    List<Payment> findAllByOrderIdIn(List<Long> orderId);
    @Query("SELECT p FROM Payment p JOIN p.order o " +
            "JOIN o.user u " +
            "WHERE p.order.id = :orderId AND u.id = :userId")
    Optional<Payment> findByOrderIdAndUserId(@Param("orderId") Long orderId, @Param("userId") Long userId);
}
