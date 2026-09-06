package gytis.courier.adapter.out.persistence.payment;

import gytis.courier.adapter.out.persistence.payment.attempt.PaymentAttemptJpaEntity;
import gytis.courier.application.port.out.payment.PaymentCommandPort;
import gytis.courier.application.port.out.payment.PaymentQueryPort;
import gytis.courier.application.readmodel.payment.UserPaymentSummaryReadModel;
import gytis.courier.domain.payment.Payment;
import gytis.courier.domain.payment.PaymentAttempt;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Optional;

@Component
public class PaymentAdapter implements PaymentCommandPort, PaymentQueryPort {
    private final PaymentJpaRepository repository;
    private final EntityManager entityManager;
    private final PaymentEntityMapper mapper;

    public PaymentAdapter(PaymentJpaRepository repository, EntityManager entityManager, PaymentEntityMapper mapper) {
        this.repository = repository;
        this.entityManager = entityManager;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public void create(Payment payment) {
        repository.save(mapper.toEntity(payment));
    }

    @Transactional
    @Override
    public Payment update(Payment payment) {
        PaymentJpaEntity managed = repository.findByOrderId(payment.getOrderId())
                .orElseThrow(() -> new IllegalStateException("Payment disappeared while doing update"));
        mapper.basicUpdate(payment, managed);

        syncAttempts(managed, payment.getPaymentAttempts());

        entityManager.flush();

        return mapper.toDomain(managed);
    }

    @Transactional
    @Override
    public Optional<Payment> findByOrderIdLocked(Long orderId) {
        System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
        return repository.findByOrderIdForUpdate(orderId)
                .map(mapper::toDomain);

    }

    @Override
    public Optional<Payment> findByOrderId(Long orderId) {
        return repository.findByOrderId(orderId)
                .map(mapper::toDomain);
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
            } else {
                for (PaymentAttemptJpaEntity attemptJpa : managed.getAttempts()) {
                    if (domain.getId().equals(attemptJpa.getId())) {
                        mapper.updateExistingAttempt(domain, attemptJpa);
                    }
                }
            }
        }
    }
}
