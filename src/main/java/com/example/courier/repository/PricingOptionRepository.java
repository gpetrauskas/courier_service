package com.example.courier.repository;

import com.example.courier.domain.PricingOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PricingOptionRepository extends JpaRepository<PricingOption, Long> {
    PricingOption findByName(String name);
}
