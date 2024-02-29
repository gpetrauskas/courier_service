package com.example.courier.repository;

import com.example.courier.domain.PricingOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PricingOptionRepository extends JpaRepository<PricingOption, Long> {
    Optional<PricingOption> findByName(String name);
}
