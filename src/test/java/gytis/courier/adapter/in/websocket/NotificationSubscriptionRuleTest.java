package gytis.courier.adapter.in.websocket;

import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.adapter.webscoket.WebSocketDestinations;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NotificationSubscriptionRuleTest {
    NotificationSubscriptionRule rule = new NotificationSubscriptionRule();

    private AuthenticatedPerson getAp(String role) {
        return new AuthenticatedPerson(1L, "person@example.com", role, "person");
    }

    @Test
    public void userIsAuthorizedForRoleNotifications() {
        AuthenticatedPerson ap = getAp("USER");

        boolean allowed = rule.authorize(WebSocketDestinations.NOTIFICATIONS + "USER", ap);

        assertThat(allowed).isTrue();
    }

    @Test
    public void courierIsNotAuthorizedForUserNotifications() {
        AuthenticatedPerson ap = getAp("COURIER");

        boolean allowed = rule.authorize(WebSocketDestinations.NOTIFICATIONS + "USER", ap);

        assertThat(allowed).isFalse();
    }

    @Test
    public void destinationWithoutRoleIsNotAuthorized() {
        AuthenticatedPerson ap = getAp("USER");

        boolean allowed = rule.authorize(WebSocketDestinations.NOTIFICATIONS, ap);

        assertThat(allowed).isFalse();
    }

    @Test
    public void userIsAuthorizedWhenDestinationRoleIsLowerCase() {
        AuthenticatedPerson ap = getAp("USER");

        boolean allowed = rule.authorize(WebSocketDestinations.NOTIFICATIONS + "user", ap);

        assertThat(allowed).isTrue();
    }

    @Test
    public void roleNotificationsDestinationIsMatched() {
        boolean match = rule.matches(WebSocketDestinations.NOTIFICATIONS);

        assertThat(match).isTrue();
    }

    @Test
    public void roleNotificationsDestinationIsNotMatched() {
        boolean match = rule.matches(WebSocketDestinations.ADMIN_TASK_CARD);

        assertThat(match).isFalse();
    }
}
