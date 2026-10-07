package gytis.courier.adapter.in.websocket;

import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.adapter.webscoket.WebSocketDestinations;
import org.springframework.stereotype.Component;

@Component
public class PersonalNotificationQueueRule implements SubscriptionRule {

    @Override
    public boolean matches(String destination) {
        return destination.equals(WebSocketDestinations.NOTIFICATION_QUEUE_SUB);
    }

    @Override
    public boolean authorize(String destination, AuthenticatedPerson person) {
        return true;
    }
}
