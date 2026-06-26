package gytis.courier.application.port.in.paymentmethod;

import gytis.courier.application.readmodel.paymentmethod.UserPaymentMethodReadModel;

import java.util.List;

public interface PaymentMethodQueryUseCase {
    List<UserPaymentMethodReadModel> savedMethods(Long userId);
    UserPaymentMethodReadModel get(Long methodId, Long userId);
}
