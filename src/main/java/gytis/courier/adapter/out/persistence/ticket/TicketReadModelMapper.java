package gytis.courier.adapter.out.persistence.ticket;

import gytis.courier.adapter.out.persistence.person.projection.UserProjection;
import gytis.courier.adapter.out.persistence.ticket.projection.TicketCommentProjection;
import gytis.courier.adapter.out.persistence.ticket.projection.TicketProjection;
import gytis.courier.application.readmodel.ticket.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TicketReadModelMapper {
    default AdminTicketReadModel toAdminReadModel(TicketProjection projection) {
        return new AdminTicketReadModel(
                toReadModel(projection),
                toCreatorReadModel(projection.getCreatedBy())
        );
    }

    TicketReadModel toReadModel(TicketProjection projection);
    CreatorSummaryReadModel toCreatorReadModel(UserProjection projection);

    TicketCommentReadModel toComment(TicketCommentProjection projection);
    AdminTicketCommentReadModel toAdminComment(TicketCommentProjection projection);
}
