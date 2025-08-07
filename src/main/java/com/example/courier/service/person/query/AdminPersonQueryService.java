package com.example.courier.service.person.query;

import com.example.courier.domain.Person;
import com.example.courier.dto.CourierDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.mapper.PersonMapper;
import com.example.courier.dto.response.person.AdminPersonResponseDTO;
import com.example.courier.repository.PersonRepository;
import com.example.courier.specification.person.PersonSpecificationBuilder;
import com.example.courier.util.PageableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@PreAuthorize("hasRole('ADMIN')")
public class AdminPersonQueryService {
    private final Logger logger = LoggerFactory.getLogger(AdminPersonQueryService.class);
    private final PersonRepository personRepository;
    private final PersonMapper mapper;

    public AdminPersonQueryService(PersonRepository personRepository, PersonMapper mapper) {
        this.personRepository = personRepository;
        this.mapper = mapper;
    }

    public PaginatedResponseDTO<AdminPersonResponseDTO> findAllPaginated(
            int page, int size, String role, String searchKeyword, String sortBy, String direction
    ) {
        Pageable pageable = PageableUtils.createPageable(page, size, sortBy, direction);
        Specification<Person> specification = PersonSpecificationBuilder.buildPersonSpecification(role, searchKeyword);

        Page<Person> personPage = personRepository.findAll(specification, pageable);
        return new PaginatedResponseDTO<>(personPage, mapper::toAdminPersonResponseDTO);
    }


    public List<CourierDTO> getAvailableCouriers() {
        Specification<Person> specification = PersonSpecificationBuilder.buildAvailableCourierSpecification();
        List<Person> personList = personRepository.findAll(specification);

        logger.info("Found " + personList.size() + " couriers available.");

        return personList.stream()
                .map(mapper::toCourierDTO)
                .toList();
    }

    public Long availableCouriersCount() {
        Specification<Person> specification = PersonSpecificationBuilder.buildAvailableCourierSpecification();
        return personRepository.countAvailableCouriers(specification);
    }
}
