package com.example.courier.repository;

import com.example.courier.domain.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PersonRepository<T extends Person> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
    Optional<T>findByEmail(String email);
    Page<T> findAll(Specification<T> specification, Pageable pageable);
    boolean existsByEmail(String email);

    @Query("SELECT COUNT(c) FROM Courier c WHERE c.hasActiveTask = false")
    long countAvailableCouriers(Specification<T> specification);

}
