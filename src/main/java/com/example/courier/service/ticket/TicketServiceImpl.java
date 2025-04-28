package com.example.courier.service.ticket;

import com.example.courier.common.TicketStatus;
import com.example.courier.domain.Person;
import com.example.courier.domain.Ticket;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.ticket.TicketCreateRequestDTO;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.repository.TicketRepository;
import com.example.courier.service.person.PersonService;
import com.example.courier.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TicketServiceImpl implements TicketService {

    private PersonService personService;
    private TicketRepository ticketRepository;

    @Autowired
    public TicketServiceImpl(PersonService personService, TicketRepository ticketRepository) {
        this.personService = personService;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public ApiResponseDTO create(TicketCreateRequestDTO requestDTO) {

        Person person = personService.findById(AuthUtils.getAuthenticatedPersonId())
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));

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
}
