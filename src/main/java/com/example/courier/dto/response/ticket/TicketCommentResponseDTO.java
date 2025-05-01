package com.example.courier.dto.response.ticket;

import java.time.LocalDateTime;

public record TicketCommentResponseDTO(
        Long id,
        String message,
        String authorName,
        LocalDateTime createdAt
) {
}
