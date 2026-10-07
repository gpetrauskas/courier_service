package gytis.courier.adapter.in.websocket;

import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.adapter.webscoket.WebSocketDestinations;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class AdminTaskCardSubscriptionRuleTest {
    AdminTaskCardSubscriptionRule rule = new AdminTaskCardSubscriptionRule();

    private AuthenticatedPerson getAp(String role) {
        return new AuthenticatedPerson(1L, "person@example.com", role, "person");
    }

    @Test
    public void adminIsAuthorizedForTaskCards() {
        AuthenticatedPerson ap = getAp("ADMIN");

        boolean allowed = rule.authorize(WebSocketDestinations.ADMIN_TASK_CARD, ap);

        assertThat(allowed).isTrue();
    }

    @Test
    public void userIsNotAuthorizedForTaskCards() {
        AuthenticatedPerson ap = getAp("USER");

        boolean allowed = rule.authorize(WebSocketDestinations.ADMIN_TASK_CARD, ap);

        assertThat(allowed).isFalse();
    }

    @Test
    void unrelatedDestinationIsNotMatched() {
        boolean ok = rule.matches("fail");

        assertThat(ok).isFalse();
    }

    @Test
    void taskCardsDestinationIsMatched() {
        boolean ok = rule.matches(WebSocketDestinations.ADMIN_TASK_CARD);

        assertThat(ok).isTrue();
    }
}
