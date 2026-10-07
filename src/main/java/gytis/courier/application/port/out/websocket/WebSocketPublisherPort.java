package gytis.courier.application.port.out.websocket;

public interface WebSocketPublisherPort {
    void broadcast(String destination, Object payload);
    void sendToUser(String email, String destination, Object payload);
}
