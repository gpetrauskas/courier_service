package com.example.courier.service.person.command;

import com.example.courier.domain.BanHistory;
import com.example.courier.domain.Person;
import com.example.courier.dto.mapper.BanHistoryMapper;
import com.example.courier.dto.request.person.BanActionRequestDTO;
import com.example.courier.dto.response.BanHistoryDTO;
import com.example.courier.repository.BanHistoryRepository;
import com.example.courier.service.person.query.PersonLookupService;
import com.example.courier.service.security.CurrentPersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@PreAuthorize("hasRole('ADMIN')")
public class BanManagementService {
    private final Logger logger = LoggerFactory.getLogger(BanManagementService.class);
    private final BanHistoryRepository banHistoryRepository;
    private final CurrentPersonService currentPersonService;
    private final PersonUpdateService updateService;
    private final PersonLookupService lookupService;
    private final BanHistoryMapper mapper;

    public BanManagementService(BanHistoryRepository banHistoryRepository, CurrentPersonService currentPersonService,
                                BanHistoryMapper mapper, PersonUpdateService updateService, PersonLookupService lookupService) {
        this.banHistoryRepository = banHistoryRepository;
        this.currentPersonService = currentPersonService;
        this.mapper = mapper;
        this.updateService = updateService;
        this.lookupService = lookupService;
    }

    @Transactional
    public String banUnban(Long personId, BanActionRequestDTO requestDTO) {
        Person person = lookupService.findNotDeletedPerson(personId);
        Person admin = currentPersonService.getCurrentPerson();

        boolean newBlockedStatus = !person.isBlocked();
        person.setBlocked(newBlockedStatus);

        updateService.persist(person);
        logBanAction(person, admin.getEmail(), requestDTO.reason(), newBlockedStatus);

        logger.info("Person ID {}, was {}.", person.getId(), person.isBlocked() ? "banned" : "unbanned");
        return generateResponseMessage(personId, newBlockedStatus);
    }

    public List<BanHistoryDTO> getBanHistory(Long personId) {
        return banHistoryRepository.findByPersonIdOrderByActionTimeDesc(personId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    private void logBanAction(Person person, String email, String reason, boolean banStatus) {
        BanHistory ban = new BanHistory(person, banStatus, email, reason);
        banHistoryRepository.save(ban);
    }

    private String generateResponseMessage(Long personId, boolean banned) {
        String action = banned ? "banned" : "unbanned";
        return String.format("Person ID %d was %s successfully", personId, action);
    }
}
