package gytis.courier.adapter.in.websocket;

import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.adapter.webscoket.WebSocketDestinations;
import gytis.courier.domain.person.Role;
import org.springframework.stereotype.Component;

@Component
public class AdminTaskCardSubscriptionRule implements SubscriptionRule {

    @Override
    public boolean matches(String destination) {
        return destination.contains(WebSocketDestinations.ADMIN_TASK_CARD);
    }

    @Override
    public boolean authorize(String destination, AuthenticatedPerson person) {
        return Role.ADMIN.name().equalsIgnoreCase(person.role());
    }
}
