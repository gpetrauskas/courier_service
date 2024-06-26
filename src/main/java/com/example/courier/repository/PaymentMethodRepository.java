package com.example.courier.repository;

import com.example.courier.domain.PaymentMethod;
import com.example.courier.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    List<PaymentMethod> findByUserId(Long id);
}
