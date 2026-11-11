package gytis.courier.application.readmodel.ticket;

public record AdminTicketCardReadModel(
        Long open,
        Long inProgress,
        Long resolved
) {
}
