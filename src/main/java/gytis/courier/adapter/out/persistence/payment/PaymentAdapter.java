package gytis.courier.adapter.out.persistence.payment;

import gytis.courier.adapter.out.persistence.payment.attempt.PaymentAttemptJpaEntity;
import gytis.courier.application.port.out.payment.PaymentCommandPort;
import gytis.courier.application.port.out.payment.PaymentQueryPort;
import gytis.courier.application.readmodel.payment.UserPaymentSummaryReadModel;
import gytis.courier.domain.payment.Payment;
import gytis.courier.domain.payment.PaymentAttempt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class PaymentAdapter implements PaymentCommandPort, PaymentQueryPort {
    private final PaymentJpaRepository repository;
    private final PaymentEntityMapper mapper;

    public PaymentAdapter(PaymentJpaRepository repository, PaymentEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public void create(Payment payment) {
        repository.save(mapper.toEntity(payment));
    }

    @Transactional
    @Override
    public void update(Payment payment) {
        PaymentJpaEntity managed = repository.findByOrderId(payment.getOrderId());
        mapper.basicUpdate(payment, managed);

        syncAttempts(managed, payment.getPaymentAttempts());
    }

    @Transactional
    @Override
    public void updateBasic(Payment payment) {
        PaymentJpaEntity managed = repository.findByOrderId(payment.getOrderId());
        mapper.basicUpdate(payment, managed);
    }

    @Override
    public Payment findByOrderId(Long orderId) {
        return mapper.toDomain(repository.findByOrderId(orderId));
    }

    @Override
    public Payment findByOrderIdWithAttempts(Long orderId) {
        return mapper.toDomain(repository.findByOrderId(orderId));
    }

    @Override
    public Optional<UserPaymentSummaryReadModel> findUserProjectionByOrderId(Long orderId) {
        return repository.findUserProjectionByOrderId(orderId).map(mapper::toUserPaymentInfoReadModel);
    }

    private void syncAttempts(PaymentJpaEntity managed, List<PaymentAttempt> domainAttempts) {
        for (PaymentAttempt domain : domainAttempts) {
            if (domain.getId() == null) {
                PaymentAttemptJpaEntity entity = mapper.toAttemptEntity(domain);
                entity.setPayment(managed);
                managed.getAttempts().add(entity);
            }
        }
    }
}
