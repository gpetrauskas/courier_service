package gytis.courier.application.port.out.paymentmethod;

import gytis.courier.application.readmodel.paymentmethod.UserPaymentMethodReadModel;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodQueryPort {
    //Optional<PaymentMethod> find(Long id, Long userId);
    Optional<UserPaymentMethodReadModel> findProjection(Long id, Long userId);
    List<UserPaymentMethodReadModel> getAll(Long userId);
}
