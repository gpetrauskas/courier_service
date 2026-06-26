package gytis.courier.adapter.in.rest.payment;

import gytis.courier.adapter.in.rest.payment.dto.*;
import gytis.courier.application.command.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentRequestMapper {

    PaymentSectionUpdateCommand toUpdateCommand(PaymentSectionUpdateRequest request);

    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "existingMethodId", source = "request.paymentMethodId")
    @Mapping(target = "command", source = "request.newPaymentMethod")
    @Mapping(target = "cvc", source = "request.cvc")
    @Mapping(target = "userId", source = "userId")
    PaymentCommand toCommand(Long userId, Long orderId, PaymentRequest request);

    default PaymentMethodCommand map(PaymentMethodRequest request) {
        if (request == null) return null;

        return switch (request) {
            case CreditCardRequest cc -> new CreditCardCommand(
                    cc.cardNumber(),
                    cc.cardHolderName(),
                    cc.expiryDate(),
                    cc.saveCard()
            );
            case PaypalRequest pp -> new PaypalCommand(
                    pp.ppEmail(),
                    pp.saved()
            );
        };
    }
}
