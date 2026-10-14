package gytis.courier.adapter.in.websocket;

import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.adapter.webscoket.WebSocketDestinations;
import gytis.courier.domain.person.Role;
import org.springframework.stereotype.Component;

@Component
public class ActivitySubscribeRule implements SubscriptionRule {

    @Override
    public boolean matches(String destination) {
        System.out.println("ee " + destination);
        System.out.println("eee " + WebSocketDestinations.ACTIVITY);
        return destination.startsWith(WebSocketDestinations.ACTIVITY);
    }

    @Override
    public boolean authorize(String destination, AuthenticatedPerson person) {
        return Role.ADMIN.name().equalsIgnoreCase(person.role());
    }
}
