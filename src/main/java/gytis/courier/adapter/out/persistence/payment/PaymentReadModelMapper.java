package gytis.courier.adapter.out.persistence.payment;

import gytis.courier.adapter.out.persistence.order.projection.PaymentProjection;
import gytis.courier.application.readmodel.payment.AdminPaymentSummaryReadModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentReadModelMapper {
    AdminPaymentSummaryReadModel toAdminSummary(PaymentProjection projection);
}
