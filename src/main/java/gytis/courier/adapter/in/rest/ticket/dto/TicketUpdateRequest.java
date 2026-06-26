package gytis.courier.adapter.in.rest.ticket.dto;

import gytis.courier.domain.ticket.TicketPriority;
import gytis.courier.domain.ticket.TicketStatus;
import gytis.courier.adapter.in.rest.common.validation.AtLeastOneField;
import gytis.courier.adapter.in.rest.common.validation.ValidEnum;

@AtLeastOneField
public record TicketUpdateRequest(
        @ValidEnum(TicketPriority.class)
        String priority,
        @ValidEnum(TicketStatus.class)
        String status
) {
}
