package com.example.courier.service.ticket;

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
import com.example.courier.repository.projection.TicketAccessIdsProjection;
import com.example.courier.service.permission.PermissionService;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.util.PageableUtils;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.function.Function;

import static com.example.courier.specification.ticket.TicketSpecification.createdBy;
import static com.example.courier.specification.ticket.TicketSpecification.hasStatus;

@Service
public class TicketServiceImpl implements TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);
    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final CurrentPersonService currentPersonService;
    private final TicketCommentRepository commentRepository;
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

        Ticket ticket = Ticket.create(requestDTO, person);

        ticketRepository.save(ticket);
        return new ApiResponseDTO("success", "Your ticket was successfully created");
    }

    @Override
    @Transactional(readOnly = true)
    public TicketBase getTicket(Long ticketId) {
        logger.info("Fetching ticket with id {}", ticketId);
        Ticket ticket = ticketRepository.findWithRelationsById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        logger.info("Check if person is admin or an authorized user");
        return currentPersonService.isAdmin() ? ticketMapper.toAdminDTO(ticket) : ticketMapper.toUserDTO(ticket);
    }

    @Override
    public PaginatedResponseDTO<? extends TicketBase> getAll(Pageable pageable, @Nullable String status, @Nullable Long personId) {
        if (status != null && !TicketStatus.isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status");
        }

        boolean isAdmin = currentPersonService.isAdmin();

        Long idFilter = isAdmin
                ? personId
                : currentPersonService.getCurrentPersonId();

        Function<Ticket, ? extends TicketBase> function = isAdmin
                ? ticketMapper::toAdminDTO
                : ticketMapper::toUserDTO;

        Page<Ticket> page = ticketRepository
                .findAll(hasStatus(status)
                        .and(createdBy(idFilter)),
                        pageable);

        return PageableUtils.mapPage(page, function);
    }

    @Override
    @Transactional
    public TicketCommentResponseDTO addComment(TicketCommentRequestDTO commentRequestDTO) {
        Person person = currentPersonService.getCurrentPerson();
        Ticket t = ticketRepository.findById(commentRequestDTO.ticketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket was not found."));

        if (!permissionService.hasTicketAccess(person, t)) {
            throw new UnauthorizedAccessException("No access to this ticket");
        }

        TicketComment comment = t.addComment(person, commentRequestDTO.message());
        ticketRepository.save(t);

        return ticketMapper.toTicketCommentResponseDTO(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponseDTO<TicketCommentResponseDTO> getComments(Long ticketId, int currentPage, int pageSize) {
        TicketAccessIdsProjection ticketAccessIdsProjection = ticketRepository.findAccessIdsById(ticketId).orElseThrow(
                () -> new ResourceNotFoundException("Ticked was not found"));

        if (!permissionService.hasTicketAccess(ticketAccessIdsProjection)) {
            throw new UnauthorizedAccessException("No access");
        }

        Pageable pageable = PageableUtils.createPageable(currentPage, pageSize, "createdAt", "asc");
        Page<TicketComment> comments = commentRepository.findByTicketId(ticketId, pageable);

        return PageableUtils.mapPage(comments, ticketMapper::toTicketCommentResponseDTO);
    }

    @Override
    @Transactional
    public void updateTicket(TicketUpdateRequestDTO requestDTO) {
        Objects.requireNonNull(requestDTO);

        Ticket ticket = ticketRepository.findById(requestDTO.id())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        switch (requestDTO.partTarget()) {
            case TicketUpdateTarget.Priority p -> ticket.changePriority(p.priority());
            case TicketUpdateTarget.Status s -> ticket.changeStatus(s.status());
        }

        ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public ApiResponseDTO close(Long ticketId) {
        Person person = currentPersonService.getCurrentPerson();
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        if (!permissionService.hasTicketAccess(person, ticket)) {
            throw new UnauthorizedAccessException("No access");
        }

        ticket.changeStatus(TicketStatus.CLOSED);

        ticketRepository.save(ticket);
        return new ApiResponseDTO("success", "Ticket was successfully closed");
    }

    /* Helper methods
     */
}
