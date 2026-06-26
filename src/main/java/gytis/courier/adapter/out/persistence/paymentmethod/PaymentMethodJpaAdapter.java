package gytis.courier.adapter.out.persistence.paymentmethod;

import gytis.courier.application.port.out.paymentmethod.PaymentMethodQueryPort;
import gytis.courier.application.readmodel.paymentmethod.UserPaymentMethodReadModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PaymentMethodJpaAdapter implements PaymentMethodQueryPort {
    private final PaymentMethodJpaRepository repository;
    private final PaymentMethodMapper mapper;

    public PaymentMethodJpaAdapter(PaymentMethodJpaRepository repository, PaymentMethodMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

/*    @Override
    public Optional<PaymentMethod> find(Long id, Long userId) {
        return repository.findByIdAndUserId(id, userId).map(mapper::toSpecificDomain);
    }*/

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
}
