package com.example.courier.repository;

import com.example.courier.domain.Courier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourierRepository extends JpaRepository<Courier, Long> {
    List<Courier> findByHasActiveTaskFalse();
}
