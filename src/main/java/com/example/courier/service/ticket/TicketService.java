package com.example.courier.service.ticket;

import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.request.ticket.TicketCommentRequestDTO;
import com.example.courier.dto.request.ticket.TicketCreateRequestDTO;
import com.example.courier.dto.request.ticket.TicketUpdateRequestDTO;
import com.example.courier.dto.response.ticket.TicketBase;
import com.example.courier.dto.response.ticket.TicketCommentResponseDTO;
import org.springframework.data.domain.Pageable;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.dto.response.ticket.TicketAdminResponseDTO;
import com.example.courier.dto.response.ticket.TicketUserResponseDTO;
import com.example.courier.exception.ResourceNotFoundException;

/**
 * Service interface for managing support tickets.
 * Defines methods for creating, updating, commenting and closing tickets.
 */
public interface TicketService {
    /**
     * Creates a new support ticket for the current user
     *
     * @param requestDTO the ticket creation request
     * @return a response indicating success message
     * @throws UnauthorizedAccessException if the current user is blocked or deleted
     */
    ApiResponseDTO create(TicketCreateRequestDTO requestDTO);

    /**
     * Returns a {@link TicketAdminResponseDTO} or {@link TicketUserResponseDTO} based on a role.
     *
     * @param ticketId a ticket identifier
     * @return a ticket as a role specific dto
     * @throws ResourceNotFoundException if ticket was not found
     */
    TicketBase getTicket(Long ticketId);

    /**
     * Returns a paginated list of a role specific tickets dto filtered by status and/or person.
     *
     * @param pageable pagination information
     * @param status optional status filter
     * @param personId optional person filter
     * @return a paginated response of tickets
     * @throws IllegalArgumentException if status is not valid
     */
    PaginatedResponseDTO<? extends TicketBase> getAll(Pageable pageable, String status, Long personId);

    /**
     * Adds a comment to an existing ticket.
     *
     * @param commentRequestDTO the comment request
     * @return a cerated comment as dto
     * @throws ResourceNotFoundException if ticket was not found
     * @throws UnauthorizedAccessException if current user has no access to given ticket
     */
    TicketCommentResponseDTO addComment(TicketCommentRequestDTO commentRequestDTO);

    /**
     * Returns all comments of specific ticket.
     *
     * @param ticketId a ticket identifier
     * @param currentPage the current page index
     * @param pageSize number of comments per page
     * @throws ResourceNotFoundException if ticket was not found
     * @throws UnauthorizedAccessException if currently logged-in user has no access to given ticket
     */
    PaginatedResponseDTO<TicketCommentResponseDTO> getComments(Long ticketId, int currentPage, int pageSize);

    /**
     * Updates a tickets status or priority based on the update request
     *
     * @param requestDTO the update request
     * @throws ResourceNotFoundException if ticket was not found
     */
    void updateTicket(TicketUpdateRequestDTO requestDTO);

    /**
     * Closes a ticket by setting its status to {@code CLOSED}.
     *
     * @param ticketId the ticket identifier
     * @return a success message
     * @throws ResourceNotFoundException if ticket was not found
     * @throws UnauthorizedAccessException if current user has no access to the ticket
     */
    ApiResponseDTO close(Long ticketId);
}
