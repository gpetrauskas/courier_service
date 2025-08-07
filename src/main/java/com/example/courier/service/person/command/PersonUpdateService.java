package com.example.courier.service.person.commands;

import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.person.UserEditDTO;
import com.example.courier.repository.PersonRepository;
import com.example.courier.service.person.query.PersonLookupService;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.service.transformation.PersonTransformationService;
import com.example.courier.validation.FieldUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonUpdateService {
    private final Logger logger = LoggerFactory.getLogger(PersonUpdateService.class);
    private final PersonRepository personRepository;
    private final PersonLookupService lookup;
    private final CurrentPersonService currentPersonService;
    private final PersonTransformationService transformationService;

    public PersonUpdateService(PersonRepository personRepository, PersonLookupService lookup,
                               CurrentPersonService currentPersonService, PersonTransformationService transformationService) {
        this.personRepository = personRepository;
        this.lookup = lookup;
        this.currentPersonService = currentPersonService;
        this.transformationService = transformationService;
    }

    @Transactional
    public ApiResponseDTO updateMyInfo(UserEditDTO dto) {
        User user = lookup.fetchPersonByIdAndType(currentPersonService.getCurrentPersonId(), User.class);

        FieldUpdater.updateIfValidOrThrow(dto.phoneNumber(), transformationService::validateAndFormatPhone, user::setPhoneNumber);
        user.getAddressById(dto.defaultAddressId()).ifPresent(user::setDefaultAddress);
        FieldUpdater.updateBoolean(dto.subscribed(), user::setSubscribed);

        personRepository.save(user);
        logger.info("User {} successfully updated his information", user.getEmail());
        return new ApiResponseDTO("success", "Successfully updated");
    }

    public void persist(Person person) {
        personRepository.save(person);
    }
}
