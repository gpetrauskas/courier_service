package com.example.courier.repository;

import com.example.courier.domain.Package;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageRepository extends JpaRepository<Package, Long> {
}
