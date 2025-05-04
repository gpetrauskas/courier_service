package com.example.courier.service.ticket;

import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.request.ticket.TicketCommentRequestDTO;
import com.example.courier.dto.request.ticket.TicketCreateRequestDTO;
import com.example.courier.dto.request.ticket.TicketUpdateRequestDTO;
import com.example.courier.dto.response.ticket.TicketBase;
import com.example.courier.dto.response.ticket.TicketCommentResponseDTO;

import java.util.List;

public interface TicketService {
    ApiResponseDTO create(TicketCreateRequestDTO requestDTO);
    TicketBase getTicket(Long ticketId);
    List<TicketBase> getAll();
    TicketCommentResponseDTO addComment(TicketCommentRequestDTO commentRequestDTO);
    PaginatedResponseDTO<TicketCommentResponseDTO> getComments(Long ticketId, int currentPage, int pageSize);
    void updateTicket(TicketUpdateRequestDTO requestDTO);
}
