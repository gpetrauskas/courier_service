package com.example.courier.service.person.account;

import com.example.courier.domain.Person;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.person.PasswordChangeDTO;
import com.example.courier.dto.response.person.PersonResponseDTO;
import com.example.courier.repository.PersonRepository;
import com.example.courier.service.person.strategy.PersonInfoStrategyResolver;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.service.validation.PersonValidationService;
import jakarta.validation.ValidationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonAccountService {
    private final PersonRepository personRepository;
    private final CurrentPersonService currentPersonService;
    private final PersonInfoStrategyResolver personInfoStrategyResolver;
    private final PasswordEncoder passwordEncoder;
    private final PersonValidationService validationService;

    public PersonAccountService(CurrentPersonService currentPersonService, PersonInfoStrategyResolver personInfoStrategyResolver,
                                PasswordEncoder passwordEncoder, PersonValidationService validationService,
                                PersonRepository personRepository) {
        this.currentPersonService = currentPersonService;
        this.personInfoStrategyResolver = personInfoStrategyResolver;
        this.passwordEncoder = passwordEncoder;
        this.validationService = validationService;
        this.personRepository = personRepository;
    }

    public PersonResponseDTO myInfo() {
        Person person = fetchCurrentPerson();
        return personInfoStrategyResolver.resolve(person);
    }

    @Transactional
    public ApiResponseDTO changePassword(PasswordChangeDTO dto) {
        if (dto == null || dto.newPassword() == null || dto.currentPassword() == null) {
            throw new ValidationException("Password fields cannot be null");
        }

        Person person = fetchCurrentPerson();
        if (!passwordEncoder.matches(dto.currentPassword(), person.getPassword())) {
            throw new ValidationException("Current password do not match.");
        }

        validationService.validatePassword(dto.newPassword());

        person.setPassword(passwordEncoder.encode(dto.newPassword()));
        personRepository.save(person);

        return new ApiResponseDTO("success", "Password updated successfully.");
    }

    private Person fetchCurrentPerson() {
        return currentPersonService.getCurrentPerson();
    }
}
