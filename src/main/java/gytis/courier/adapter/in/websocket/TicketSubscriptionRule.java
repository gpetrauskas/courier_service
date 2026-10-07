package gytis.courier.adapter.in.websocket;

import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.adapter.webscoket.WebSocketDestinations;
import gytis.courier.application.port.in.ticket.TicketCommentQueryUseCase;
import org.springframework.stereotype.Component;

@Component
public class TicketSubscriptionRule implements SubscriptionRule {
    private final TicketCommentQueryUseCase useCase;

    public TicketSubscriptionRule(TicketCommentQueryUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public boolean matches(String destination) {
        return destination.contains(WebSocketDestinations.TICKET);
    }

    @Override
    public boolean authorize(String destination, AuthenticatedPerson person) {
        return canAccess(person, destination);
    }

    private boolean canAccess(AuthenticatedPerson person, String destination) {
        long id;
        String stringId = destination.substring(destination.lastIndexOf("/") + 1);
        try {
            id = Long.parseLong(stringId);
        } catch (NumberFormatException e) {
            return false;
        }

        return useCase.canAccessTicket(id, person.id(), person.role());
    }
}
