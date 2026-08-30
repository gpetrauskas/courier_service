package gytis.courier.config;

import gytis.courier.adapter.in.security.AuthenticatedPerson;
import gytis.courier.application.port.in.ticket.TicketCommentQueryUseCase;
import gytis.courier.application.port.out.ticket.TicketCommentQueryPort;
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
    private static final String NOTIFICATIONS = "/topic/notifications/";
    private static final String TICKET = "/topic/tickets/";

    private final TicketCommentQueryUseCase ticketCommentQueryUseCase;

    public MyChannelInterceptor(TicketCommentQueryUseCase ticketCommentQueryUseCase) {
        this.ticketCommentQueryUseCase = ticketCommentQueryUseCase;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand()) && accessor.getDestination().startsWith(NOTIFICATIONS)) {
            AuthenticatedPerson authenticatedPerson = getAP(accessor);
            //String destination = accessor.getDestination().substring(accessor.getDestination().lastIndexOf("/") + 1);

            if (!getDestination(accessor).equalsIgnoreCase(authenticatedPerson.role())) {
                return null;
            }
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand()) && accessor.getDestination().startsWith(TICKET)) {
            if (!canAccess(accessor)) {
                return null;
            }
        }
        return message;
    }

    private boolean canAccess(StompHeaderAccessor accessor) {
        AuthenticatedPerson ap = getAP(accessor);
        Long ticketId;
        try {
            ticketId = Long.valueOf(getDestination(accessor));
        } catch (NumberFormatException e) {
            return false;
        }
        return ticketCommentQueryUseCase.canAccessTicket(ticketId, ap.id(), ap.role());
    }

    private String getDestination(StompHeaderAccessor accessor) {
        return accessor.getDestination().substring(accessor.getDestination().lastIndexOf("/") + 1);
    }

    private AuthenticatedPerson getAP(StompHeaderAccessor accessor) {
        Principal principal = accessor.getUser();
        AuthenticatedPerson ap = (AuthenticatedPerson) ((Authentication) principal).getPrincipal();
        return ap;
    }
}
