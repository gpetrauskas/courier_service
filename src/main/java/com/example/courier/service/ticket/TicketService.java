package com.example.courier.service.ticket;

import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.ticket.TicketCreateRequestDTO;
import com.example.courier.dto.response.ticket.TicketBase;

import java.util.List;

public interface TicketService {
    ApiResponseDTO create(TicketCreateRequestDTO requestDTO);
    TicketBase getTicket(Long ticketId);
    List<TicketBase> getAll();
}
