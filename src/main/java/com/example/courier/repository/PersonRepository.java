package com.example.courier.repository;

import com.example.courier.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person>findByEmail(String email);
}
