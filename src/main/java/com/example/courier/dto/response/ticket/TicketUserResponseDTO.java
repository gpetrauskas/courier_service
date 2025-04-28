package com.example.courier.dto.response.ticket;

import com.example.courier.common.TicketPriority;
import com.example.courier.common.TicketStatus;

import java.time.LocalDateTime;

public record TicketUserResponseDTO(
        Long id,
        String title,
        String description,
        TicketStatus status,
        TicketPriority priority,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements TicketBase {
}
