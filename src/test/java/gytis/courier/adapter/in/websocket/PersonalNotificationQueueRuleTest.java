package gytis.courier.adapter.in.websocket;

import gytis.courier.adapter.webscoket.WebSocketDestinations;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PersonalNotificationQueueRuleTest {
    PersonalNotificationQueueRule rule = new PersonalNotificationQueueRule();

    @Test
    public void personalQueueDestinationIsMatched() {
        boolean matched = rule.matches(WebSocketDestinations.NOTIFICATION_QUEUE_SUB);

        assertThat(matched).isTrue();
    }

    @Test
    public void unrelatedDestinationIsNotMatched() {
        boolean matched = rule.matches(WebSocketDestinations.TICKET);

        assertThat(matched).isFalse();
    }
}
