package gytis.courier.adapter.in.websocket;

import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.adapter.webscoket.WebSocketDestinations;
import gytis.courier.application.port.in.ticket.TicketCommentQueryUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketSubscriptionRuleTest {
    @Mock
    private TicketCommentQueryUseCase ticketCommentQueryUseCase;

    TicketSubscriptionRule rule;
    AuthenticatedPerson ap;

    @BeforeEach
    void setUp() {
         rule = new TicketSubscriptionRule(ticketCommentQueryUseCase);
         ap = new AuthenticatedPerson(1L, "person@example.com", "ADMIN", "person");
    }

    @Test
    public void ticketSubscriptionAuthorized() {
        when(ticketCommentQueryUseCase.canAccessTicket(3L, ap.id(), ap.role())).thenReturn(true);

        boolean allowed = rule.authorize(WebSocketDestinations.TICKET + 3L, ap);

        assertThat(allowed).isTrue();
    }

    @Test
    public void ticketSubscriptionNotAuthorizedDestinationHasNoId() {
        boolean allowed = rule.authorize(WebSocketDestinations.TICKET + "randomString", ap);

        assertThat(allowed).isFalse();
        verifyNoInteractions(ticketCommentQueryUseCase);
    }

    @Test
    public void ticketSubscriptionNotAuthorized() {
        when(ticketCommentQueryUseCase.canAccessTicket(3L, ap.id(), ap.role())).thenReturn(false);

        boolean allowed = rule.authorize(WebSocketDestinations.TICKET + 3L, ap);

        assertThat(allowed).isFalse();
    }

    @Test
    public void ticketSubscriptionNotMatch() {
        boolean match = rule.matches(WebSocketDestinations.ADMIN_TASK_CARD);

        assertThat(match).isFalse();
    }

    @Test
    public void ticketSubscriptionMatch() {
        boolean match = rule.matches(WebSocketDestinations.TICKET);

        assertThat(match).isTrue();
    }
}
