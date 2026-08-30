package gytis.courier.adapter.out.websocket;

import gytis.courier.application.port.out.ticket.TicketCommentBroadcastPort;
import gytis.courier.domain.ticket.TicketComment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketRealTimePublisher implements TicketCommentBroadcastPort {
    private final SimpMessagingTemplate template;
    private static final String TICKET_TOPIC_PREFIX = "/topic/tickets/";

    public WebSocketRealTimePublisher(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Override
    public void broadcast(Long ticketId, TicketComment comment) {
        template.convertAndSend(TICKET_TOPIC_PREFIX + ticketId, comment);
    }


}
