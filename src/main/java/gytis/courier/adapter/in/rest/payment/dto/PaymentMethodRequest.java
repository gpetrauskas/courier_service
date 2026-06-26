package gytis.courier.adapter.in.rest.payment.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreditCardRequest.class, name = "creditCard"),
        @JsonSubTypes.Type(value = PaypalRequest.class, name = "payPal")
})
public sealed interface PaymentMethodRequest permits CreditCardRequest, PaypalRequest{

}
