package gytis.courier.application.port.in.order;

public interface CancelOrderUseCase {
    void cancel(Long orderId, Long userId);
}
