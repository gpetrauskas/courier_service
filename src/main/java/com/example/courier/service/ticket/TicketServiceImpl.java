package com.example.courier.service.ticket;

import com.example.courier.common.TicketStatus;
import com.example.courier.domain.Person;
import com.example.courier.domain.Ticket;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.mapper.TicketMapper;
import com.example.courier.dto.request.ticket.TicketCreateRequestDTO;
import com.example.courier.dto.response.ticket.TicketBase;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.repository.TicketRepository;
import com.example.courier.service.security.CurrentPersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);
    private TicketRepository ticketRepository;
    private TicketMapper ticketMapper;
    private final CurrentPersonService currentPersonService;

    @Autowired
    public TicketServiceImpl(
            TicketRepository ticketRepository,
            TicketMapper ticketMapper,
            CurrentPersonService currentPersonService
    ) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
        this.currentPersonService = currentPersonService;
    }

    @Override
    @Transactional
    public ApiResponseDTO create(TicketCreateRequestDTO requestDTO) {
        Person person = currentPersonService.getCurrentPerson();
        if (person.isBlocked() || person.isDeleted()) {
            throw new UnauthorizedAccessException("cannot create ticket");
        }

        System.out.println(requestDTO);

        Ticket ticket = new Ticket();
        ticket.setTitle(requestDTO.title());
        ticket.setDescription(requestDTO.description());
        ticket.setPriority(requestDTO.priority());
        ticket.setCreatedBy(person);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedAt(LocalDateTime.now());

        ticketRepository.save(ticket);
        return new ApiResponseDTO("success", "Your ticket was successfully created");
    }

    @Override
    @Transactional(readOnly = true)
    public TicketBase getTicket(Long ticketId) {
        logger.info("Fetching ticket with id {}", ticketId);
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        logger.info("Check if person is admin or an authorized user");
        Person me = currentPersonService.getCurrentPerson();
        if (me.getRole().equals("ADMIN")) {
            logger.info("Is admin");
            return ticketMapper.toAdminDTO(ticket);
        } else if (ticket.getCreatedBy().getId().equals(me.getId())) {
            logger.info("is creator of ticket");
            return ticketMapper.toUserDTO(ticket);
        } else {
            logger.info("neither admin or creator of ticket");
            throw new AccessDeniedException("No Access");
        }
    }

    @Override
    public List<TicketBase> getAll() {
        Person person = currentPersonService.getCurrentPerson();
        List<Ticket> list = ticketRepository.findAllByCreatedById(person.getId());
        return list.stream()
                .map(t -> ticketMapper.toUserDTO(t)).collect(Collectors.toList());
    }
}
