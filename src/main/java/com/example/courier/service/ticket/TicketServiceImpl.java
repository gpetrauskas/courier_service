package com.example.courier.service.ticket;

import com.example.courier.common.TicketPriority;
import com.example.courier.common.TicketStatus;
import com.example.courier.domain.Person;
import com.example.courier.domain.Ticket;
import com.example.courier.domain.TicketComment;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.mapper.TicketMapper;
import com.example.courier.dto.request.ticket.TicketCommentRequestDTO;
import com.example.courier.dto.request.ticket.TicketCreateRequestDTO;
import com.example.courier.dto.request.ticket.TicketUpdateRequestDTO;
import com.example.courier.dto.response.ticket.TicketBase;
import com.example.courier.dto.response.ticket.TicketCommentResponseDTO;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.repository.TicketCommentRepository;
import com.example.courier.repository.TicketRepository;
import com.example.courier.service.permission.PermissionService;
import com.example.courier.service.security.CurrentPersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);
    private TicketRepository ticketRepository;
    private TicketMapper ticketMapper;
    private final CurrentPersonService currentPersonService;
    private TicketCommentRepository commentRepository;
    private final PermissionService permissionService;

    @Autowired
    public TicketServiceImpl(
            TicketRepository ticketRepository,
            TicketMapper ticketMapper,
            CurrentPersonService currentPersonService,
            TicketCommentRepository commentRepository,
            PermissionService permissionService
    ) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
        this.currentPersonService = currentPersonService;
        this.commentRepository = commentRepository;
        this.permissionService = permissionService;
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
        } else if (permissionService.hasTicketAccess(me, ticket)) {
            logger.info("is creator of ticket");
            return ticketMapper.toUserDTO(ticket);
        } else {
            logger.info("neither admin or creator of ticket");
            throw new AccessDeniedException("No Access");
        }
    }

    @Override
    public PaginatedResponseDTO<? extends TicketBase> getAll(Pageable pageable) {

        Person person = currentPersonService.getCurrentPerson();
        Page<Ticket> list;
        if (person.getRole().equals("ADMIN")) {
            list = ticketRepository.findAll(pageable);
        } else {
            list = ticketRepository.findAllByCreatedById(person.getId(), pageable);
        }
        return new PaginatedResponseDTO<>(
                list.stream().map(ticketMapper::toUserDTO).toList(),
                list.getNumber(),
                list.getTotalElements(),
                list.getTotalPages()
        );
    }

    @Override
    @Transactional
    public TicketCommentResponseDTO addComment(TicketCommentRequestDTO commentRequestDTO) {

        Person person = currentPersonService.getCurrentPerson();
        Ticket t = ticketRepository.findById(commentRequestDTO.ticketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket was not found."));

        if (!permissionService.hasTicketAccess(person, t) || t.getStatus().equals(TicketStatus.CLOSED)) {
            throw new UnauthorizedAccessException("No access");
        }

        TicketComment ticketComment = new TicketComment();
        ticketComment.setTicket(t);
        ticketComment.setMessage(commentRequestDTO.message());
        ticketComment.setAuthor(person);
        ticketComment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(ticketComment);

        t.setUpdatedAt(LocalDateTime.now());
        ticketRepository.save(t);

        return ticketMapper.toTicketCommentResponseDTO(ticketComment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponseDTO<TicketCommentResponseDTO> getComments(Long ticketId, int currentPage, int pageSize) {
        Person p = currentPersonService.getCurrentPerson();
        Ticket t = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        if (!permissionService.hasTicketAccess(p, t)) {
            throw new UnauthorizedAccessException("No access");
        }

        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<TicketComment> comments = commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId, pageable);
        Page<TicketCommentResponseDTO> responseDTOS = comments.map(ticketMapper::toTicketCommentResponseDTO);

        return new PaginatedResponseDTO<>(
                responseDTOS.getContent(),
                comments.getNumber(),
                comments.getTotalElements(),
                comments.getTotalPages()
        );
    }

    @Override
    public void updateTicket(TicketUpdateRequestDTO requestDTO) {
        if (!currentPersonService.isAdmin()) {
            throw new UnauthorizedAccessException("No access");
        }

        Objects.requireNonNull(requestDTO);
        Ticket ticket = ticketRepository.findById(requestDTO.id())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        switch (requestDTO.partTarget()) {
            case TicketUpdateTarget.Status(TicketStatus newStatus) -> {
                if (!TicketStatus.isValidTransition(ticket.getStatus(), newStatus)) {
                    throw new IllegalStateException("Invalid status transition " + ticket.getStatus() + " -> " + newStatus);
                }
                ticket.setStatus(newStatus);
            }
            case TicketUpdateTarget.Priority(TicketPriority newPriority) -> {
                if (!TicketPriority.isValidPriority(newPriority.name())) {
                    throw new IllegalStateException("Invalid priority data");
                }
                ticket.setPriority(TicketPriority.valueOf(newPriority.name()));
            }
        }
        ticketRepository.save(ticket);
    }
}
