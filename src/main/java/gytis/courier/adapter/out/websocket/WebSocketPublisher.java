package gytis.courier.adapter.out.websocket;

import gytis.courier.application.port.out.websocket.WebSocketPublisherPort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketPublisher implements WebSocketPublisherPort {
    private final SimpMessagingTemplate template;

    public WebSocketPublisher(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Override
    public void broadcast(String destination, Object payload) {
        template.convertAndSend(destination, payload);
    }

    @Override
    public void sendToUser(String email, String destination, Object payload) {
        template.convertAndSendToUser(email, destination, payload);
    }
}
