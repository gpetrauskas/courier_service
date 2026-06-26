package gytis.courier.application.port.in.payment;

public interface CancelPaymentUseCase {
    void cancelByOrderId(Long orderId);
}
