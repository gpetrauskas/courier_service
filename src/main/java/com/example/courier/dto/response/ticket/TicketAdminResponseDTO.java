package com.example.courier.dto.response.ticket;

import com.example.courier.common.TicketPriority;
import com.example.courier.common.TicketStatus;
import com.example.courier.dto.response.person.PersonResponseDTO;

import java.time.LocalDateTime;

public record TicketAdminResponseDTO(
        Long id,
        String title,
        String description,
        TicketStatus status,
        TicketPriority priority,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        PersonResponseDTO createdBy,
        PersonResponseDTO assignedTo
) {
}
