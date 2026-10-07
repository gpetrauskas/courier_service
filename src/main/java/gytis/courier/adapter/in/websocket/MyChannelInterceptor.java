package gytis.courier.adapter.in.websocket;

import gytis.courier.adapter.in.security.AuthenticatedPerson;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

@Component
public class MyChannelInterceptor implements ChannelInterceptor {
    private final List<SubscriptionRule> rules;

    public MyChannelInterceptor(List<SubscriptionRule> rules) {
        this.rules = rules;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        System.out.println("im here? accesor null ?"  + accessor);
        if (accessor == null) return message;

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            System.out.println("destination" + destination);
            if (destination == null || destination.isBlank()) {
                System.out.println("not here");
                    return null;
            }

            AuthenticatedPerson ap = getAp(accessor);
            if (ap == null) {
                return null;
            }

            boolean allowed = rules.stream()
                    .filter(r -> r.matches(destination))
                    .anyMatch(r -> r.authorize(destination, ap));

            System.out.println("found allowed? " + allowed);
            return allowed ? message : null;
        }

        return message;
    }

    private AuthenticatedPerson getAp(StompHeaderAccessor accessor) {
        Principal principal = accessor.getUser();

        if (principal instanceof Authentication a && a.getPrincipal() instanceof AuthenticatedPerson ap) {
            return ap;
        } else {
            return null;
        }
    }
}
