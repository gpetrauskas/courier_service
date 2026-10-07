package gytis.courier.adapter.in.websocket;

import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.adapter.webscoket.WebSocketDestinations;
import org.springframework.stereotype.Component;

@Component
public class NotificationSubscriptionRule implements SubscriptionRule {

    @Override
    public boolean matches(String destination) {
        return destination.contains(WebSocketDestinations.NOTIFICATIONS);
    }

    @Override
    public boolean authorize(String destination, AuthenticatedPerson person) {
        return getDestination(destination).equalsIgnoreCase(person.role());
    }

    private String getDestination(String destination) {
        String qq =  destination.substring(destination.lastIndexOf("/") + 1);
        System.out.println("show destination in rule " + qq);

        return qq;
    }
}
