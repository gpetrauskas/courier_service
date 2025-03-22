package com.example.courier.service.person;

import com.example.courier.domain.Courier;
import com.example.courier.domain.Person;
import com.example.courier.dto.CourierDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.PersonResponseDTO;
import com.example.courier.dto.mapper.PersonMapper;
import com.example.courier.dto.request.PersonDetailsUpdateRequest;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.PersonRepository;
import com.example.courier.service.RegistrationService;
import com.example.courier.specification.person.PersonSpecificationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PersonServiceImpl<T extends Person> implements PersonService<T> {

    private static final Logger logger = LoggerFactory.getLogger(PersonServiceImpl.class);
    private final PersonRepository<T> personRepository;
    private final PersonMapper personMapper;

    public PersonServiceImpl(PersonRepository<T> personRepository, PersonMapper personMapper,
                             RegistrationService registrationService) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
    }

    @Override
    public Optional<T> findById(Long id) {
        return personRepository.findById(id);
    }

    @Override
    public void updatePassword(Long id, String newPassword) {
    }
























    public PaginatedResponseDTO<PersonResponseDTO> findAllPaginated(int page, int size, String role, String searchKeyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Specification<T> specification = PersonSpecificationBuilder.buildPersonSpecification(role, searchKeyword);

        Page<T> personPage = personRepository.findAll(specification, pageable);

        return mapToPaginatedResponseDTO(personPage);
    }

    private PaginatedResponseDTO<PersonResponseDTO> mapToPaginatedResponseDTO(Page<T> personPage) {
        List<PersonResponseDTO> content = personPage.getContent()
                .stream()
                .map(personMapper::toPersonResponseDTO)
                .toList();

        return new PaginatedResponseDTO<>(
                content, personPage.getNumber(),
                personPage.getTotalElements(), personPage.getTotalPages()
        );
    }

    @Transactional
    public void updateDetails(Long personId, PersonDetailsUpdateRequest updateRequest) {
        T person = fetchById(personId);

        if (!person.getId().equals(updateRequest.id())) {
            throw new IllegalArgumentException("Person ID in request does not match the path Id");
        }
        
        personMapper.updatePersonFromRequest(updateRequest, person);
        personRepository.save(person);
    }

    public List<CourierDTO> getAvailableCouriers() {
        Specification<T> specification = PersonSpecificationBuilder.buildAvailableCourierSpecification();
        List<T> personList = personRepository.findAll(specification);

        return personList.stream()
                .map(personMapper::toCourierDTO)
                .toList();
    }

    public Long availableCouriersCount() {
        Specification<T> specification = PersonSpecificationBuilder.buildAvailableCourierSpecification();
        return personRepository.countAvailableCouriers(specification);
    }

    @Override
    public void save(T person) {
        personRepository.save(person);
    }

    public void hasCourierActiveTask(Courier courier) {
        if (courier.hasActiveTask()) throw new IllegalArgumentException("Courier already have assigned task.");
    }





    @Transactional
    public void delete(Long personId) {
        T person = fetchById(personId);
        if (person.isDeleted()) {
            throw new IllegalStateException("User already deleted.");
        }

        person.setDeleted(true);
        personRepository.save(person);
        logger.info("Person ID {}, deleted", personId);
    }

    public String banUnban(Long id) {
        T person = fetchById(id);
        if (person.isDeleted()) {
            throw new IllegalStateException("Cannot ban/unban deleted user");
        }

        person.setBlocked(!person.isBlocked());
        personRepository.save(person);
        logger.info("Person ID {}, was {}.",id, person.isBlocked() ? "banned" : "unbanned");
        return person.isBlocked() ? "User was banned successfully." : "User was unbanned successfully.";
    }

    private T fetchById(Long personId) {
        return personRepository.findById(personId).orElseThrow(() ->
                new ResourceNotFoundException("User was not found."));
    }

    public <T extends Person> T fetchPersonByIdAndType(Long id, Class<T> personType) {
        Person person = fetchById(id);

        return Optional.of(person)
                .filter(personType::isInstance)
                .map(personType::cast)
                .orElseThrow(() -> new IllegalArgumentException("The person is no instance of " + personType.getSimpleName()));
    }
}
