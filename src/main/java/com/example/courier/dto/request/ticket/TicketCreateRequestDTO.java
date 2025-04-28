package com.example.courier.dto.request.ticket;

import com.example.courier.common.TicketPriority;

public record TicketCreateRequestDTO(
        String title,
        String description,
        TicketPriority priority
) {
}
