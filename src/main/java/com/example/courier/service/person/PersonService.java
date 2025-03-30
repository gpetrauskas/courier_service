package com.example.courier.service.person;

import com.example.courier.domain.Person;

import java.util.Optional;

public interface PersonService {
    Optional<Person> findById(Long id);
    void updatePassword(Long personId, String newPassword);
    void save(Person person);
    boolean checkIfPersonAlreadyExistsByEmail(String email);
    <T extends Person> T fetchPersonByIdAndType(Long id, Class<T> personType);
}
