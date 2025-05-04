package com.example.courier.service.ticket;

import com.example.courier.common.TicketPriority;
import com.example.courier.common.TicketStatus;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "kind"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TicketUpdateTarget.Status.class, name = "STATUS"),
        @JsonSubTypes.Type(value = TicketUpdateTarget.Priority.class, name = "PRIORITY")
})
public sealed interface TicketUpdateTarget {
    record Status(TicketStatus status) implements TicketUpdateTarget {}
    record Priority(TicketPriority priority) implements TicketUpdateTarget {}
}
