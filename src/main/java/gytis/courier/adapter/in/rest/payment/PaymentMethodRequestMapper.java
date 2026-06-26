package gytis.courier.adapter.in.rest.payment;

import gytis.courier.adapter.in.rest.payment.dto.CreditCardRequest;
import gytis.courier.adapter.in.rest.payment.dto.PaypalRequest;
import gytis.courier.application.command.CreditCardCommand;
import gytis.courier.application.command.PaypalCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMethodRequestMapper {
    CreditCardCommand toCommand(CreditCardRequest request);
    PaypalCommand toCommand(PaypalRequest request);
}
