package com.example.courier.service.person;

import com.example.courier.domain.*;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.CourierDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.mapper.BanHistoryMapper;
import com.example.courier.dto.mapper.PersonMapper;
import com.example.courier.dto.request.PersonDetailsUpdateRequest;
import com.example.courier.dto.request.person.PasswordChangeDTO;
import com.example.courier.dto.request.person.UserEditDTO;
import com.example.courier.dto.response.BanHistoryDTO;
import com.example.courier.dto.response.person.AdminPersonResponseDTO;
import com.example.courier.dto.response.person.PersonResponseDTO;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.BanHistoryRepository;
import com.example.courier.repository.PersonRepository;
import com.example.courier.specification.person.PersonSpecificationBuilder;
import com.example.courier.util.AuthUtils;
import com.example.courier.util.PageableUtils;
import com.example.courier.validation.PasswordValidator;
import com.example.courier.validation.PhoneValidator;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {

    private static final Logger logger = LoggerFactory.getLogger(PersonServiceImpl.class);
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final BanHistoryRepository banHistoryRepository;
    private final BanHistoryMapper banHistoryMapper;
    private final PhoneValidator phoneValidator;
    private final PasswordValidator passwordValidator;
    private final PasswordEncoder passwordEncoder;

    public PersonServiceImpl(PersonRepository personRepository, PersonMapper personMapper,
                             BanHistoryRepository banHistoryRepository, BanHistoryMapper banHistoryMapper,
                             PhoneValidator phoneValidator, PasswordValidator passwordValidator,
                             PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
        this.banHistoryRepository = banHistoryRepository;
        this.banHistoryMapper = banHistoryMapper;
        this.phoneValidator = phoneValidator;
        this.passwordValidator = passwordValidator;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<Person> findById(Long id) {
        return personRepository.findById(id);
    }

    @Override
    public void updatePassword(Long id, String newPassword) {
    }

    @Override
    public PersonResponseDTO myInfo() {
        Person person = fetchById(AuthUtils.getAuthenticatedPersonId());
        return personMapper.toDto(person);
    }

    @Override
    @Transactional
    public ApiResponseDTO updateMyInfo(UserEditDTO dto) {
        User user = fetchPersonByIdAndType(AuthUtils.getAuthenticatedPersonId(), User.class);

        dto.phoneNumber().map(phoneValidator::validate).ifPresent(user::setPhoneNumber);

        dto.defaultAddressId().flatMap(defaultId -> user.getAddresses().stream()
                .filter(a -> a.getId().equals(defaultId))
                .findFirst())
                .ifPresent(user::setDefaultAddress);

        dto.subscribed().ifPresent(user::setSubscribed);

        personRepository.save(user);

        return new ApiResponseDTO("success", "Successfully updated");
    }

    public ApiResponseDTO changePassword(PasswordChangeDTO dto) {
        Person person = fetchById(AuthUtils.getAuthenticatedPersonId());
        if (!passwordEncoder.matches(dto.currentPassword(), person.getPassword())) {
            throw new ValidationException("Current password do not match.");
        }

        passwordValidator.validatePassword(dto.newPassword());

        person.setPassword(passwordEncoder.encode(dto.newPassword()));
        save(person);

        return new ApiResponseDTO("success", "Password updated successfully.");
    }




















    @Override
    public PaginatedResponseDTO<AdminPersonResponseDTO> findAllPaginated(
            int page, int size, String role, String searchKeyword, String sortBy, String direction
    ) {
        Pageable pageable = PageableUtils.createPageable(page, size, sortBy, direction);
        Specification<Person> specification = PersonSpecificationBuilder.buildPersonSpecification(role, searchKeyword);

        Page<Person> personPage = personRepository.findAll(specification, pageable);

        return new PaginatedResponseDTO<>(personPage, personMapper::toAdminPersonResponseDTO);
    }

    @Override
    @Transactional
    public void updateDetails(Long personId, PersonDetailsUpdateRequest updateRequest) {
        Person person = fetchById(personId);
        
        personMapper.updatePersonFromRequest(updateRequest, person);
        personRepository.save(person);

        logger.info("Updated person with ID {}", person.getId());
    }

    public List<CourierDTO> getAvailableCouriers() {
        Specification<Person> specification = PersonSpecificationBuilder.buildAvailableCourierSpecification();
        List<Person> personList = personRepository.findAll(specification);

        logger.info("Found " + personList.size() + " couriers available.");

        return personList.stream()
                .map(personMapper::toCourierDTO)
                .toList();
    }

    public Long availableCouriersCount() {
        Specification<Person> specification = PersonSpecificationBuilder.buildAvailableCourierSpecification();
        return personRepository.countAvailableCouriers(specification);
    }

    @Override
    public void save(Person person) {
        personRepository.save(person);
    }

    public void hasCourierActiveTask(Courier courier) {
        if (courier.hasActiveTask()) throw new IllegalArgumentException("Courier already have assigned task.");
    }

    public boolean checkIfPersonAlreadyExistsByEmail(String email) {
        return personRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public void delete(Long personId) {
        Person person = findNotDeletedPerson(personId);
        person.delete();

        personRepository.save(person);
        logger.info("Person with ID {}, deleted successfully", personId);
    }

    public String banUnban(Long id) {
        Person person = findNotDeletedPerson(id);

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

        return Optional.of(person)
                .filter(personType::isInstance)
                .map(personType::cast)
                .orElseThrow(() -> new IllegalArgumentException("The person is no instance of " + personType.getSimpleName()));
    }

    public <T extends Person> List<T> fetchAllByType(Class<T> personType) {
        return personRepository.findAllByType(personType);
    }

    public <T extends Person> List<T> getAllActiveByType(Class<T> tClass) {
        return personRepository.findAllActiveByType(tClass);
    }

    public List<BanHistoryDTO> getBanHistory(Long personId) {
        Person person = fetchById(personId);
        List<BanHistory> banHistories = banHistoryRepository.findByPersonOrderByActionTimeDesc(person);
        if (banHistories.isEmpty()) {
            return List.of();
        }

        return banHistories.stream()
                .map(banHistoryMapper::toDTO)
                .toList();
    }

    public List<Long> findAllActiveIdsByType(Class<? extends Person> type) {
        return personRepository.findAllActiveIdsByType(type);
    }

    private Person findNotDeletedPerson(Long id) {
        return personRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("User was not found"));
    }

}
