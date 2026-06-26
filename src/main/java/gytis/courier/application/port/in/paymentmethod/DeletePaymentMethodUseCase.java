package gytis.courier.application.port.in.paymentmethod;

public interface DeletePaymentMethodUseCase {
    void delete(Long methodId, Long userId);
}
