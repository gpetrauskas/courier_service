package gytis.courier.adapter.out.persistence.paymentmethod;

import gytis.courier.domain.payment.method.Paypal;
import org.springframework.stereotype.Component;

@Component
public class PaypalFactory extends Paypal{
    protected PaypalFactory() {
        super();
    }

    public Paypal recover() {
        return new PaypalFactory();
    }
}
