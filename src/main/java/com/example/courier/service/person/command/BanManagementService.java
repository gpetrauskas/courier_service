package com.example.courier.service.person.commands;

import com.example.courier.domain.BanHistory;
import com.example.courier.domain.Person;
import com.example.courier.dto.mapper.BanHistoryMapper;
import com.example.courier.dto.request.person.BanActionRequestDTO;
import com.example.courier.dto.response.BanHistoryDTO;
import com.example.courier.repository.BanHistoryRepository;
import com.example.courier.repository.PersonRepository;
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
    private final PersonRepository personRepository;
    private final BanHistoryRepository banHistoryRepository;
    private final CurrentPersonService currentPersonService;
    private final BanHistoryMapper mapper;

    public BanManagementService(PersonRepository personRepository, BanHistoryRepository banHistoryRepository,
                                CurrentPersonService currentPersonService, BanHistoryMapper mapper) {
        this.personRepository = personRepository;
        this.banHistoryRepository = banHistoryRepository;
        this.currentPersonService = currentPersonService;
        this.mapper = mapper;
    }

    @Transactional
    public String banUnban(Person person, BanActionRequestDTO requestDTO) {
        person.setBlocked(!person.isBlocked());
        personRepository.save(person);

        logBanAction(person, requestDTO);

        logger.info("Person ID {}, was {}.",person.getId(), person.isBlocked() ? "banned" : "unbanned");
        return person.isBlocked() ? "User was banned successfully." : "User was unbanned successfully.";
    }

    public List<BanHistoryDTO> getBanHistory(Long personId) {
        return banHistoryRepository.findByPersonIdOrderByActionTimeDesc(personId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    private void logBanAction(Person person, BanActionRequestDTO requestDTO) {
        String adminEmail = currentPersonService.getCurrentPerson().getEmail();
        BanHistory banHistory = new BanHistory(person, person.isBlocked(), adminEmail, requestDTO.reason());

        banHistoryRepository.save(banHistory);
    }
}
