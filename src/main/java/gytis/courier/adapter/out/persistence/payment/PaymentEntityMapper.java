package gytis.courier.adapter.out.persistence.payment;

import gytis.courier.adapter.out.persistence.payment.attempt.PaymentAttemptJpaEntity;
import gytis.courier.adapter.out.persistence.payment.projection.PaymentUserProjection;
import gytis.courier.application.readmodel.payment.UserPaymentSummaryReadModel;
import gytis.courier.domain.payment.Payment;
import gytis.courier.domain.payment.PaymentAttempt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PaymentEntityMapper {
    @Mapping(target = "id", ignore = true)
    PaymentJpaEntity toEntity(Payment domain);
    default Payment toDomain(PaymentJpaEntity entity) {
        return Payment.restore(
                entity.id,
                entity.getOrderId(),
                entity.getAmount(),
                entity.getStatus(),
                entity.getAttempts().stream()
                        .map(this::toAttemptDomain)
                        .toList()
        );
    }

    default PaymentAttempt toAttemptDomain(PaymentAttemptJpaEntity entity) {
        return PaymentAttempt.restore(
                entity.getId(),
                entity.getStatus(),
                entity.getProviderType(),
                entity.getTransactionId(),
                entity.getFailureReason(),
                entity.getCreatedAt()
        );
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attempts", ignore = true)
    void basicUpdate(Payment payment, @MappingTarget PaymentJpaEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "providerType", source = "provider")
    PaymentAttemptJpaEntity toAttemptEntity(PaymentAttempt domain);

    UserPaymentSummaryReadModel toUserPaymentInfoReadModel(PaymentUserProjection projection);
}
