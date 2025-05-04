package com.example.courier.dto.request.ticket;

import com.example.courier.service.ticket.TicketUpdateTarget;

public record TicketUpdateRequestDTO(
        Long id,
        TicketUpdateTarget partTarget
) {
}
