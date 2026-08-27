package gytis.courier.config;

import gytis.courier.adapter.in.security.AuthenticatedPerson;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class MyChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;
        System.out.println("user type: " + accessor.getUser().getClass().getName());
        System.out.println("command: " + accessor.getCommand());
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand()) && accessor.getDestination().startsWith("/topic/notifications/")) {
            Principal principal = accessor.getUser();
            AuthenticatedPerson authenticatedPerson = (AuthenticatedPerson) ((Authentication) principal).getPrincipal();
            String destination = accessor.getDestination().substring(accessor.getDestination().lastIndexOf("/") + 1);
            System.out.println("dest: " + destination);
            if (!destination.equalsIgnoreCase(authenticatedPerson.role())) {
                return null;
            }
        }
        return message;
    }
}
