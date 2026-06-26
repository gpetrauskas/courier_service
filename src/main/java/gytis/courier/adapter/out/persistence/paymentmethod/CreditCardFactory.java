package gytis.courier.adapter.out.persistence.paymentmethod;

import gytis.courier.domain.payment.method.CreditCard;
import org.mapstruct.ObjectFactory;
import org.springframework.stereotype.Component;

@Component
public class CreditCardFactory extends CreditCard {

    protected CreditCardFactory() {
        super();
    }

    @ObjectFactory
    public CreditCard recover() {
        return new CreditCardFactory();
    }
}
