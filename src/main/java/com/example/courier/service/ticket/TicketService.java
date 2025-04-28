package com.example.courier.service.ticket;

import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.ticket.TicketCreateRequestDTO;

public interface TicketService {
    ApiResponseDTO create(TicketCreateRequestDTO requestDTO);
}
