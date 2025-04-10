package com.example.courier.repository;

import com.example.courier.domain.Admin;
import com.example.courier.domain.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {
    <S extends Person> S save(S entity);

    Optional<Person>findByEmail(String email);
    @Query("SELECT p FROM Person p WHERE p.isDeleted = false")
    Page<Person> findAll(Specification<Person> specification, Pageable pageable);
    boolean existsByEmail(String email);

    @Query("SELECT COUNT(c) FROM Courier c WHERE c.hasActiveTask = false")
    long countAvailableCouriers(Specification<Person> specification);

    @Query("SELECT p FROM Person p WHERE TYPE(p) = :type")
    <T extends Person> List<T> findAllByType(@Param("type") Class<T> type);

    @Query("SELECT p FROM Person p WHERE p.isDeleted = false AND p.isBlocked = false")
    List<Person> findAllActive();

    @Query("SELECT p FROM Person p WHERE TYPE(p) = :type AND p.isBlocked = false AND p.isBlocked = false")
    <T extends Person> List<T> findAllActiveByType(@Param("type") Class<T> type);

}
