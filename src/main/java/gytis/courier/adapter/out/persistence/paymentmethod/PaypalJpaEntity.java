package gytis.courier.adapter.out.persistence.paymentmethod;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PAYPAL")
public class PaypalJpaEntity extends PaymentMethodJpaEntity {
    @Column
    private String ppEmail;

    protected PaypalJpaEntity() {}

    public String getPpEmail() { return ppEmail; }

    public void setPpEmail(String ppEmail) {this.ppEmail = ppEmail; }
}
