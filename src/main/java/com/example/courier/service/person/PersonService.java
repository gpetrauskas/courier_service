package com.example.courier.service.person;

import com.example.courier.domain.Person;

import java.util.Optional;

public interface PersonService<T extends Person> {
    Optional<T> findById(Long id);
    void updatePassword(Long personId, String newPassword);
    void save(T person);
}
