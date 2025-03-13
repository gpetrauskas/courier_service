package com.example.courier.service.person;

import com.example.courier.domain.Courier;
import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import com.example.courier.dto.CourierDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.PersonResponseDTO;
import com.example.courier.dto.mapper.PersonMapper;
import com.example.courier.dto.request.PersonDetailsUpdateRequest;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.PersonRepository;
import com.example.courier.specification.person.PersonSpecificationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PersonService {

    private static final Logger logger = LoggerFactory.getLogger(PersonService.class);
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    public PersonService(PersonRepository personRepository, PersonMapper personMapper) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
    }

    public PaginatedResponseDTO<PersonResponseDTO> findAllPaginated(int page, int size, String role, String searchKeyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Specification<Person> specification = PersonSpecificationBuilder.buildPersonSpecification(role, searchKeyword);

        Page<Person> personPage = personRepository.findAll(specification, pageable);

        return mapToPaginatedResponseDTO(personPage);
    }

    private PaginatedResponseDTO<PersonResponseDTO> mapToPaginatedResponseDTO(Page<Person> personPage) {
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
        Person person = fetchById(personId);

        if (!person.getId().equals(updateRequest.id())) {
            throw new IllegalArgumentException("Person ID in request does not match the path Id");
        }
        
        personMapper.updatePersonFromRequest(updateRequest, person);
        personRepository.save(person);
    }

    @Transactional
    public void delete(Long personId) {
        Person person = fetchById(personId);
        if (person.isDeleted()) {
            throw new IllegalStateException("User already deleted.");
        }

        person.setDeleted(true);
        personRepository.save(person);
        logger.info("Person ID {}, deleted", personId);
    }

    public String banUnban(Long id) {
        Person person = fetchById(id);
        if (person.isDeleted()) {
            throw new IllegalStateException("Cannot ban/unban deleted user");
        }

        person.setBlocked(!person.isBlocked());
        personRepository.save(person);
        logger.info("Person ID {}, was {}.",id, person.isBlocked() ? "banned" : "unbanned");
        return person.isBlocked() ? "User was banned successfully." : "User was unbanned successfully.";
    }

    private Person fetchById(Long personId) {
        return personRepository.findById(personId).orElseThrow(() ->
                new ResourceNotFoundException("User was not found."));
    }

    public <T extends Person> T fetchPersonByIdAndType(Long id, Class<T> personType) {
        Person person = fetchById(id);
        if (!personType.isInstance(person)) {
            throw new IllegalArgumentException("The person is not instance of " + personType.getSimpleName());
        }

        return personType.cast(person);
    }
}
