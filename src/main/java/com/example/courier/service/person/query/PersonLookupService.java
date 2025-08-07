package com.example.courier.service.person.query;

import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.exception.UserNotFoundException;
import com.example.courier.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonLookupService {
    private final PersonRepository personRepository;

    public PersonLookupService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person findByUsername(String username) {
        return personRepository.findByEmail(username).orElseThrow(() ->
                new UserNotFoundException("Person not found with username/email: " + username));
    }

    public List<Long> findAllActiveIdsByType(Class<? extends Person> type) {
        return personRepository.findAllActiveIdsByType(type);
    }

    public Person findNotDeletedPerson(Long id) {
        return personRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("User was not found"));
    }

    public <T extends Person> List<T> fetchAllByType(Class<T> personType) {
        return personRepository.findAllByType(personType);
    }

    public <T extends Person> List<T> getAllActiveByType(Class<T> tClass) {
        return personRepository.findAllActiveByType(tClass);
    }

    public <T extends Person> T fetchPersonByIdAndType(Long id, Class<T> personType) {
        Person person = fetchById(id);
        return convertToType(person, personType);
    }

    public Person fetchById(Long personId) {
        return personRepository.findById(personId).orElseThrow(() ->
                new ResourceNotFoundException("User was not found."));
    }

    public boolean checkIfPersonAlreadyExistsByEmail(String email) {
        return personRepository.existsByEmail(email);
    }

    public User findUserByIdWithAddresses(Long userId) {
        return personRepository.findUserByIdWithAddresses(userId).orElseThrow(() ->
                new ResourceNotFoundException("User was not found"));
    }

    private <T extends Person> T convertToType(Person person, Class<T> personType) {
        return Optional.of(person)
                .filter(personType::isInstance)
                .map(personType::cast)
                .orElseThrow(() -> new IllegalArgumentException("The person is no instance of " + personType.getSimpleName()));
    }
}
