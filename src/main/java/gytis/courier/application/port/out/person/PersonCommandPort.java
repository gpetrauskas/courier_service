package gytis.courier.application.port.out.person;

import gytis.courier.domain.person.Person;

import java.util.Optional;

public interface PersonCommandPort {
    Optional<Person> findById(Long personId);
    Optional<Person> findByEmail(String email);
    void save(Person person);
    //PageResult<Person> findAllPaginated(PersonQuery query);
}
