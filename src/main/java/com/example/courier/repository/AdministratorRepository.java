package com.example.courier.repository;

import com.example.courier.domain.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
    Administrator findByUsername(String username);

}
