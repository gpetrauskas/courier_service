package com.example.courier.dto.request.ticket;

public record TicketCommentRequestDTO(
        Long ticketId,
        String message
) {
}
