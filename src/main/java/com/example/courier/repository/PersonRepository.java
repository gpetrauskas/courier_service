package com.example.courier.repository;

import com.example.courier.domain.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {
    <S extends Person> S save(S entity);

    Optional<Person>findByEmail(String email);
    @Query("SELECT p FROM Person p WHERE p.isDeleted = false")
    Page<Person> findAll(Specification<Person> specification, Pageable pageable);
    boolean existsByEmail(String email);

    @Query("SELECT COUNT(c) FROM Courier c WHERE c.hasActiveTask = false")
    long countAvailableCouriers(Specification<Person> specification);

}
