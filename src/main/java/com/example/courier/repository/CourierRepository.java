package com.example.courier.repository;

import com.example.courier.domain.Courier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CourierRepository extends JpaRepository<Courier, Long>, JpaSpecificationExecutor<Courier> {
    List<Courier> findByHasActiveTaskFalse();
}
