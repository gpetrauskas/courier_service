package com.example.courier.service.person.strategy;

import com.example.courier.domain.Admin;
import com.example.courier.domain.Person;
import com.example.courier.dto.mapper.PersonMapper;
import com.example.courier.dto.response.person.PersonResponseDTO;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.AdminRepository;
import org.springframework.stereotype.Component;

@Component
public class AdminInfoStrategy implements PersonInfoStrategy {
    private final AdminRepository adminRepository;
    private final PersonMapper personMapper;

    public AdminInfoStrategy(AdminRepository adminRepository, PersonMapper personMapper) {
        this.adminRepository = adminRepository;
        this.personMapper = personMapper;
    }

    @Override
    public boolean supports(Person person) {
        return (person instanceof Admin);
    }

    @Override
    public PersonResponseDTO map(Person person) {
        Admin admin = adminRepository.findById(person.getId()).orElseThrow(() ->
                new ResourceNotFoundException("Not found"));
        return personMapper.toAdminProfile(admin);
    }
}
