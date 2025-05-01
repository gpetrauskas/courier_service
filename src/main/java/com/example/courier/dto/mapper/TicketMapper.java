package com.example.courier.dto.mapper;

import com.example.courier.domain.Ticket;
import com.example.courier.domain.TicketComment;
import com.example.courier.dto.response.ticket.TicketAdminResponseDTO;
import com.example.courier.dto.response.ticket.TicketCommentResponseDTO;
import com.example.courier.dto.response.ticket.TicketUserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = PersonMapper.class)
public interface TicketMapper {

    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "toPersonDto")
    @Mapping(target = "assignedTo", source = "assignedTo", qualifiedByName = "toPersonDto")
    TicketAdminResponseDTO toAdminDTO(Ticket ticket);

    TicketUserResponseDTO toUserDTO(Ticket ticket);

    @Mapping(target = "authorName", source = "author.name")
    TicketCommentResponseDTO toTicketCommentResponseDTO(TicketComment comment);
}
