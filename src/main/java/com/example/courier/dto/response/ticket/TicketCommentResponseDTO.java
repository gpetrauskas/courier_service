package com.example.courier.dto.response.ticket;

import java.time.LocalDateTime;

public record TicketCommentResponseDTO(
        Long id,
        String message,
        LocalDateTime createdAt
) {
}
