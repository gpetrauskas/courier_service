package com.example.courier.service.person;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.List;

/** Facade for all person related operations.
 * Acts as single entry point for controller, delegating for specified services
 */
@Service
public class PersonFacade {

    private static final Logger logger = LoggerFactory.getLogger(PersonFacade.class);
    private final BanManagementService banManager;
    private final PersonUpdateService personUpdate;
    private final PersonAccountService accountService;
    private final AdminPersonQueryService adminQueries;
    private final AdminPersonCommandService adminCommands;

    public PersonFacade(
            BanManagementService banManager,
            PersonUpdateService updater,
            PersonAccountService accountService,
            AdminPersonQueryService adminPersonQueryService,
            AdminPersonCommandService adminPersonCommandService
    ) {
        this.banManager = banManager;
        this.personUpdate = updater;
        this.accountService = accountService;
        this.adminQueries = adminPersonQueryService;
        this.adminCommands = adminPersonCommandService;
    }

    /* Account services
    */

    /** Delegates to {@link PersonAccountService#myInfo()} */
    public PersonResponseDTO myInfo() {
        return accountService.myInfo();
    }

    /** Delegate to {@link PersonAccountService#changePassword(PasswordChangeDTO)} */
    public ApiResponseDTO changePassword(PasswordChangeDTO dto) {
        return accountService.changePassword(dto);
    }

    /* Update services
    */
    
    /** Delegate to {@link PersonUpdateService#updateMyInfo(UserEditDTO)} */
    public ApiResponseDTO updateMyInfo(UserEditDTO dto) {
        return personUpdate.updateMyInfo(dto);
    }

    /* Admin queries
    */
    
    /** Delegate to {@link AdminPersonQueryService#findAllPaginated(int, int, String, String, String, String)} */
    public PaginatedResponseDTO<AdminPersonResponseDTO> findAllPaginated(
            int page, int size, String role, String searchKeyword, String sortBy, String direction
    ) {
        return adminQueries.findAllPaginated(page, size, role, searchKeyword, sortBy, direction);
    }
    
    /** Delegates to {@link AdminPersonQueryService#getAvailableCouriers()} */
    public List<CourierDTO> getAvailableCouriers() {
        return adminQueries.getAvailableCouriers();
    }

    /** Delegates to {@link AdminPersonQueryService#availableCouriersCount()} */
    public Long availableCouriersCount() {
        return adminQueries.availableCouriersCount();
    }

    /* Admin command
    */

    /** Delegates to {@link AdminPersonCommandService#updateDetails(Long, PersonDetailsUpdateRequest)} */
    public void updateDetails(Long personId, PersonDetailsUpdateRequest updateRequest) {
        adminCommands.updateDetails(personId, updateRequest);
    }

    /** Delegates to {@link AdminPersonCommandService#softDelete(Long)} */
    public void delete(Long personId) {
        adminCommands.softDelete(personId);
    }

    /* Ban manager
    */

    /** Delegates to {@link BanManagementService#banUnban(Long, BanActionRequestDTO)}*/
    public String banUnban(Long id, BanActionRequestDTO requestDTO) {
        return banManager.banUnban(id, requestDTO);
    }

    /** Delegates to {@link BanManagementService#getBanHistory(Long)} */
    public List<BanHistoryDTO> getBanHistory(Long personId) {
        return banManager.getBanHistory(personId);
    }
}
