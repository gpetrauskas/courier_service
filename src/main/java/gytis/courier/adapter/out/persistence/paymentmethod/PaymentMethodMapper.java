package gytis.courier.adapter.out.persistence.paymentmethod;

import gytis.courier.adapter.out.persistence.paymentmethod.projection.PaymentMethodProjection;
import gytis.courier.application.readmodel.paymentmethod.CreditCardReadModel;
import gytis.courier.application.readmodel.paymentmethod.PaypalReadModel;
import gytis.courier.application.readmodel.paymentmethod.UserPaymentMethodReadModel;
import gytis.courier.domain.payment.method.CreditCard;
import gytis.courier.domain.payment.method.PaymentMethod;
import gytis.courier.domain.payment.method.Paypal;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = { /*CreditCardFactory.class, PaypalFactory.class*/ })
public interface PaymentMethodMapper {
/*
    void updatePaymentMethodEntity(PaymentMethod method, @MappingTarget PaymentMethodJpaEntity entity);
*/

    // to specific entity
    CreditCardJpaEntity toCCEntity(CreditCard domain);
    PaypalJpaEntity toPPEntity(Paypal domain);
    @Named("resolvePaymentMethodEntity")
    default PaymentMethodJpaEntity toSpecificEntity(PaymentMethod domain) {
        if (domain instanceof CreditCard cc) return toCCEntity(cc);
        if (domain instanceof Paypal pp) return toPPEntity(pp);
        throw new IllegalArgumentException("Wrong method");
    }

    // to specific domain
    @Named("resolvePaymentMethodDomain")
    default PaymentMethod toSpecificDomain(PaymentMethodJpaEntity entity) {
        if (entity instanceof CreditCardJpaEntity cc) {
            return CreditCard.recover(
                    cc.getId(), cc.isSaved(), cc.getToken(),
                    cc.getLast4(), cc.getExpiryDate(), cc.getCardHolderName()
            );
        }
        if (entity instanceof PaypalJpaEntity pp) {
            return Paypal.recover(
                    pp.getId(), pp.isSaved(), pp.getToken(), pp.getPpEmail()
            );
        }
        throw new IllegalArgumentException("Unknown type");
    }

    @IterableMapping(qualifiedByName = "resolvePaymentMethodDomain")
    @Named("resolvePaymentMethodDomainList")
    List<PaymentMethod> toPaymentMethodList(List<PaymentMethodJpaEntity> entities);

    // read models
    default UserPaymentMethodReadModel toReadModel(PaymentMethodProjection projection) {
        return switch (projection.getPaymentType()) {
            case "CREDIT_CARD" -> toCcReadModel(projection);
            case "PAYPAL" -> toPpReadModel(projection);
            default -> throw new IllegalArgumentException("Unknown type: " + projection.getPaymentType());
        };
    }

    @Mapping(source = "paymentType", target = "type")
    CreditCardReadModel toCcReadModel(PaymentMethodProjection projection);

    @Mapping(source = "paymentType", target = "type")
    PaypalReadModel toPpReadModel(PaymentMethodProjection projection);
}
