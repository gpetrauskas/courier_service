package com.example.courier.repository;

import com.example.courier.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<User, Long> {
}
