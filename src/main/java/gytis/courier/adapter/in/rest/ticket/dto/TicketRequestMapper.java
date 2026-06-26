package gytis.courier.adapter.in.rest.ticket.dto;

import gytis.courier.adapter.common.CommonValueObjectMapper;
import gytis.courier.application.command.AddTicketCommentCommand;
import gytis.courier.application.command.CreateTicketCommand;
import gytis.courier.application.command.UpdateTicketCommand;
import gytis.courier.domain.ticket.TicketPriority;
import gytis.courier.domain.ticket.TicketStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(componentModel = "spring", uses = { CommonValueObjectMapper.class })
public interface TicketRequestMapper {
    default CreateTicketCommand toCommandCreate(TicketCreateRequest request, Long personId) {
        return new CreateTicketCommand(
                request.title(),
                request.description(),
                TicketPriority.valueOf(request.priority().toUpperCase()),
                personId
        );
    }

    AddTicketCommentCommand toCommentAddCommand(Long ticketId, Long personId, String role, AddCommentRequest request);

    @Mapping(target = "priority", expression = "java(mapPriority(request.priority()))")
    @Mapping(target = "status", expression = "java(mapStatus(request.status()))")
    UpdateTicketCommand toUpdateCommand(TicketUpdateRequest request);

    default Optional<TicketPriority> mapPriority(String priority) {
        return priority == null ? Optional.empty() :
                Optional.of(TicketPriority.valueOf(priority));
    }

    default Optional<TicketStatus> mapStatus(String status) {
        return status == null ? Optional.empty() :
                Optional.of(TicketStatus.valueOf(status));
    }
}
