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
import com.example.courier.service.address.AddressService;
import com.example.courier.specification.person.PersonSpecificationBuilder;
import com.example.courier.util.AuthUtils;
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

    public PersonResponseDTO myInfo() {
        Person person = fetchById(AuthUtils.getAuthenticatedPersonId());
        return personMapper.toDto(person);
    }

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




















    public PaginatedResponseDTO<AdminPersonResponseDTO> findAllPaginated(int page, int size, String role, String searchKeyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Specification<Person> specification = PersonSpecificationBuilder.buildPersonSpecification(role, searchKeyword);

        Page<Person> personPage = personRepository.findAll(specification, pageable);

        return mapToPaginatedResponseDTO(personPage);
    }

    private PaginatedResponseDTO<AdminPersonResponseDTO> mapToPaginatedResponseDTO(Page<Person> personPage) {
        List<AdminPersonResponseDTO> content = personPage.getContent()
                .stream()
                .map(personMapper::toAdminPersonResponseDTO)
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

    public List<CourierDTO> getAvailableCouriers() {
        Specification<Person> specification = PersonSpecificationBuilder.buildAvailableCourierSpecification();
        List<Person> personList = personRepository.findAll(specification);

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

}
