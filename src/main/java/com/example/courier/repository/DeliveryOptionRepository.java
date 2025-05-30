package com.example.courier.repository;

import com.example.courier.domain.DeliveryMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface DeliveryOptionRepository extends JpaRepository<DeliveryMethod, Long> {
    DeliveryMethod findByName(String name);

    @Query("SELECT p.description FROM DeliveryMethod p WHERE p.description LIKE %:keyword%")
    Set<String> findDeliveryPreferences(@Param("keyword") String keyword);

    boolean existsByName(String name);
}
