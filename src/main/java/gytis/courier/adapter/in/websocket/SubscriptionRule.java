package gytis.courier.adapter.in.websocket;

import gytis.courier.adapter.in.security.AuthenticatedPerson;

public interface SubscriptionRule {
    boolean matches(String destination);
    boolean authorize(String destination, AuthenticatedPerson person);
}
