package com.example.courier.repository;

import com.example.courier.domain.PricingOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PricingOptionRepository extends JpaRepository<PricingOption, Long> {
    PricingOption findByName(String name);

    @Query("SELECT p.description FROM PricingOption p WHERE p.description LIKE %:keyword%")
    Set<String> findDeliveryPreferences(@Param("keyword") String keyword);
}
