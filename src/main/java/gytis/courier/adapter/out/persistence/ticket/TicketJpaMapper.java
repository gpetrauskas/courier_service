package gytis.courier.adapter.out.persistence.ticket;

import gytis.courier.domain.ticket.Ticket;
import gytis.courier.domain.ticket.TicketComment;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TicketJpaMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdById", source = "createdBy")
    @Mapping(target = "createdBy", ignore = true)
    TicketJpaEntity toEntity(Ticket domain);

    @Mapping(target = "ticket", ignore = true)
    TicketCommentJpaEntity toEntity(TicketComment domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdById", ignore = true)
    void updateEntity(Ticket domain, @MappingTarget TicketJpaEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdById", ignore = true)
    @Mapping(target = "comments", ignore = true)
    void updateEntityWithComments(Ticket domain, @MappingTarget TicketJpaEntity entity);

    @AfterMapping
    default void setTicketOnComments(@MappingTarget TicketJpaEntity entity) {
        if (entity.getComments() != null) {
            entity.getComments().forEach(c -> c.setTicket(entity));
        }
    }

    @AfterMapping
    default void mapComments(TicketJpaEntity entity, @MappingTarget Ticket ticket) {
        if (entity.getComments() != null) {
            entity.getComments().forEach(c -> ticket.getComments().add(toDomain(c)));
        }
    }

    default TicketComment toDomain(TicketCommentJpaEntity entity) {
        if (entity == null) return null;

        return TicketComment.restore(
                entity.getId(),
                entity.getAuthorId(),
                entity.getMessage(),
                entity.getCreatedAt()
        );
    }

    default Ticket toDomain(TicketJpaEntity entity) {
        if (entity == null) return null;

        return Ticket.restore(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getPriority(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getCreatedById(),
                entity.getAssignedTo(),
                null
        );
    }
}
