package gytis.courier.application.readmodel.ticket;

public record AdminTicketReadModel(
        TicketReadModel ticket,
        CreatorSummaryReadModel creator
) {
}
