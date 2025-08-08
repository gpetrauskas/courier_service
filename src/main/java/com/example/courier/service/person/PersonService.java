package com.example.courier.service.person;

import com.example.courier.domain.Courier;
import com.example.courier.domain.Person;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.CourierDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.request.PersonDetailsUpdateRequest;
import com.example.courier.dto.request.person.BanActionRequestDTO;
import com.example.courier.dto.request.person.PasswordChangeDTO;
import com.example.courier.dto.request.person.UserEditDTO;
import com.example.courier.dto.response.BanHistoryDTO;
import com.example.courier.dto.response.person.AdminPersonResponseDTO;
import com.example.courier.dto.response.person.PersonResponseDTO;
import com.example.courier.service.person.account.PersonAccountService;
import com.example.courier.service.person.command.AdminPersonCommandService;
import com.example.courier.service.person.command.BanManagementService;
import com.example.courier.service.person.command.PersonUpdateService;
import com.example.courier.service.person.query.AdminPersonQueryService;
import com.example.courier.service.person.query.PersonLookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    private static final Logger logger = LoggerFactory.getLogger(PersonService.class);
    private final PersonLookupService lookupService;
    private final BanManagementService banManager;
    private final PersonUpdateService personUpdate;
    private final PersonAccountService accountService;
    private final AdminPersonQueryService adminQueries;
    private final AdminPersonCommandService adminCommands;

    public PersonService(
            PersonLookupService lookup,
            BanManagementService banManager,
            PersonUpdateService updater,
            PersonAccountService accountService,
            AdminPersonQueryService adminPersonQueryService,
            AdminPersonCommandService adminPersonCommandService
    ) {
        this.lookupService = lookup;
        this.banManager = banManager;
        this.personUpdate = updater;
        this.accountService = accountService;
        this.adminQueries = adminPersonQueryService;
        this.adminCommands = adminPersonCommandService;
    }

    // queries
    public PersonResponseDTO myInfo() {
        return accountService.myInfo();
    }

    public ApiResponseDTO updateMyInfo(UserEditDTO dto) {
        return personUpdate.updateMyInfo(dto);
    }

    public ApiResponseDTO changePassword(PasswordChangeDTO dto) {
        return accountService.changePassword(dto);
    }

    public PaginatedResponseDTO<AdminPersonResponseDTO> findAllPaginated(
            int page, int size, String role, String searchKeyword, String sortBy, String direction
    ) {
        return adminQueries.findAllPaginated(page, size, role, searchKeyword, sortBy, direction);
    }

    public void updateDetails(Long personId, PersonDetailsUpdateRequest updateRequest) {
        adminCommands.updateDetails(personId, updateRequest);
    }

    public List<CourierDTO> getAvailableCouriers() {
        return adminQueries.getAvailableCouriers();
    }

    public Long availableCouriersCount() {
        return adminQueries.availableCouriersCount();
    }

    public void hasCourierActiveTask(Courier courier) {
        if (courier.hasActiveTask()) throw new IllegalArgumentException("Courier already have assigned task.");
    }

    public boolean checkIfPersonAlreadyExistsByEmail(String email) {
        return lookupService.checkIfPersonAlreadyExistsByEmail(email);
    }

    public void delete(Long personId) {
        adminCommands.softDelete(personId);
    }

    public String banUnban(Long id, BanActionRequestDTO requestDTO) {
        return banManager.banUnban(id, requestDTO);
    }

    public List<BanHistoryDTO> getBanHistory(Long personId) {
        return banManager.getBanHistory(personId);
    }

    public Person findByUsername(String username) {
        return lookupService.findByUsername(username);
    }

    public List<Long> findAllActiveIdsByType(Class<? extends Person> type) {
        return lookupService.findAllActiveIdsByType(type);
    }

    public <T extends Person> List<T> fetchAllByType(Class<T> personType) {
        return lookupService.fetchAllByType(personType);
    }

    public <T extends Person> List<T> getAllActiveByType(Class<T> tClass) {
        return lookupService.getAllActiveByType(tClass);
    }

    public <T extends Person> T fetchPersonByIdAndType(Long id, Class<T> personType) {
        return lookupService.fetchPersonByIdAndType(id, personType);
    }

    public void save(Person person) {
        personUpdate.persist(person);
    }
}
