package gytis.courier.adapter.out.persistence.person.management;

import gytis.courier.adapter.out.persistence.person.PersonEntityMapper;
import gytis.courier.adapter.out.persistence.person.common.PersonJpaEntity;
import gytis.courier.adapter.out.persistence.person.common.PersonJpaRepository;
import gytis.courier.application.port.out.person.PersonCommandPort;
import gytis.courier.domain.person.Person;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class PersonAdapter implements PersonCommandPort {
    private final PersonJpaRepository repository;
    private final PersonEntityMapper mapper;

    public PersonAdapter(PersonJpaRepository repository, PersonEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Person> findById(Long personId) {
        return repository.findById(personId)
                .map(mapper::toSpecificBasicDomain);
    }

    @Override
    public Optional<Person> findByEmail(String email) {
        return repository.findPersonByEmail(email)
                .map(mapper::toSpecificBasicDomain);
    }

    @Transactional
    @Override
    public void save(Person person) {
        PersonJpaEntity manage = repository.findById(person.getId()).orElseThrow();
        mapper.updateEntityFromDomain(person, manage);
    }
}
