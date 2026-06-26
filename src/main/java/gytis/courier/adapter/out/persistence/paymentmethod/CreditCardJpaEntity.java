package gytis.courier.adapter.out.persistence.paymentmethod;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

@Entity
@DiscriminatorValue("CREDIT_CARD")
public class CreditCardJpaEntity extends PaymentMethodJpaEntity {
/*    @Column
    private String token;*/

    @Column(nullable = false)
    private String last4;

    @Column(nullable = false)
    private String expiryDate;

    @Column(nullable = false)
    private String cardHolderName;

    @Transient
    private String cardNumber;



    protected CreditCardJpaEntity() {}

/*
    public String getToken() { return token; }
*/
    public String getLast4() { return last4; }
    public String getExpiryDate() { return expiryDate; }
    public String getCardHolderName() { return cardHolderName; }

/*
    public void setToken(String token) { this.token = token; }
*/
    public void setLast4(String last4) { this.last4 = last4; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }
}
