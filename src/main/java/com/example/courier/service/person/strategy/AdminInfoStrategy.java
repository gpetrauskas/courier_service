package com.example.courier.service.person.strategy;

import com.example.courier.common.Role;
import com.example.courier.domain.Admin;
import com.example.courier.dto.mapper.PersonMapper;
import com.example.courier.dto.response.person.PersonResponseDTO;
import com.example.courier.service.person.query.PersonLookupService;
import org.springframework.stereotype.Component;

@Component
public class AdminInfoStrategy implements PersonInfoStrategy {
    private final PersonLookupService personLookupService;
    private final PersonMapper personMapper;

    public AdminInfoStrategy(PersonLookupService personLookupService, PersonMapper personMapper) {
        this.personLookupService = personLookupService;
        this.personMapper = personMapper;
    }

    @Override
    public Role supportsType() {
        return Role.ADMIN;
    }

    @Override
    public PersonResponseDTO map(Long personId) {
        Admin person = personLookupService.findAdminById(personId);

        return personMapper.toAdminProfile(person);
    }
}
