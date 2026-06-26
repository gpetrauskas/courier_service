package gytis.courier.application.command;

public record AddTicketCommentCommand(
        Long ticketId,
        Long personId,
        String role,
        String message
) {
}
