package com.example.courier.service.person;

import com.example.courier.domain.Person;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.person.PasswordChangeDTO;
import com.example.courier.dto.response.person.PersonResponseDTO;
import com.example.courier.repository.PersonRepository;
import com.example.courier.service.person.strategy.PersonInfoStrategy;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.service.validation.PersonValidationService;
import jakarta.validation.ValidationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PersonAccountService {
    private final PersonRepository personRepository;
    private final CurrentPersonService currentPersonService;
    private final List<PersonInfoStrategy> strategies;
    private final PasswordEncoder passwordEncoder;
    private final PersonValidationService validationService;

    public PersonAccountService(CurrentPersonService currentPersonService, List<PersonInfoStrategy> strategies,
                                PasswordEncoder passwordEncoder, PersonValidationService validationService,
                                PersonRepository personRepository) {
        this.currentPersonService = currentPersonService;
        this.strategies = strategies;
        this.passwordEncoder = passwordEncoder;
        this.validationService = validationService;
        this.personRepository = personRepository;
    }

    public PersonResponseDTO myInfo() {
        Person person = currentPersonService.getCurrentPerson();
        return strategies.stream()
                .filter(s -> s.supports(person))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("no handler"))
                .map(person);
    }

    @Transactional
    public ApiResponseDTO changePassword(PasswordChangeDTO dto) {
        Person person = currentPersonService.getCurrentPerson();
        if (!passwordEncoder.matches(dto.currentPassword(), person.getPassword())) {
            throw new ValidationException("Current password do not match.");
        }

        validationService.validatePassword(dto.newPassword());

        person.setPassword(passwordEncoder.encode(dto.newPassword()));
        personRepository.save(person);

        return new ApiResponseDTO("success", "Password updated successfully.");
    }
}
