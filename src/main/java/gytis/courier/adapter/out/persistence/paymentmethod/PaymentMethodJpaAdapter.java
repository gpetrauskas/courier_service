package gytis.courier.adapter.out.persistence.paymentmethod;

import gytis.courier.application.port.out.paymentmethod.PaymentMethodCommandPort;
import gytis.courier.application.port.out.paymentmethod.PaymentMethodQueryPort;
import gytis.courier.application.readmodel.paymentmethod.UserPaymentMethodReadModel;
import gytis.courier.domain.payment.method.PaymentMethod;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PaymentMethodJpaAdapter implements PaymentMethodQueryPort, PaymentMethodCommandPort {
    private final PaymentMethodJpaRepository repository;
    private final PaymentMethodMapper mapper;

    public PaymentMethodJpaAdapter(PaymentMethodJpaRepository repository, PaymentMethodMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<UserPaymentMethodReadModel> findProjection(Long id, Long userId) {
        return repository.findProjectedByIdAndUserId(id, userId)
                .map(mapper::toReadModel);
    }

    @Override
    public List<UserPaymentMethodReadModel> getAll(Long userId) {
        System.out.println("as cia");

        var test = repository.findAllByUserIdAndSavedTrue(userId);

        System.out.println(test.size() + " thi is returned count");

        return repository.findAllByUserIdAndSavedTrue(userId).stream()
                .map(mapper::toReadModel)
                .toList();
    }

    @Override
    public Optional<PaymentMethod> findByIdAndUserId(Long id, Long userId) {
        return repository.findByIdAndUserId(id, userId)
                .map(mapper::toSpecificDomain);
    }
}
