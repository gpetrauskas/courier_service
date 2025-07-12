package com.example.courier.service.person.strategy;

import com.example.courier.domain.Person;
import com.example.courier.dto.response.person.PersonResponseDTO;

public interface PersonInfoStrategy {
    boolean supports(Person person);
    PersonResponseDTO map(Person person);
}
