package gytis.courier.adapter.in.rest.ticket.dto;

import gytis.courier.adapter.in.rest.common.validation.ValidTicketPriority;

public record TicketCreateRequest(
        String title,
        String description,
        @ValidTicketPriority String priority
) {
}
