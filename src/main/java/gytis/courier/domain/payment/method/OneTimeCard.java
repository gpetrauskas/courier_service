package gytis.courier.domain.payment.method;

import gytis.courier.domain.payment.ProviderType;

public final class OneTimeCard extends PaymentMethod {
    private final String cardNumber;
    private final String expiryDate;
    private final String cardHolderName;

    private OneTimeCard(String cardNumber, String expiryDate, String cardHolderName) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cardHolderName = cardHolderName;
    }

    @Override
    public ProviderType providerType() { return ProviderType.CREDIT_CARD; }

    @Override
    public void validate(String cvc) {

    }

    public String getCardNumber() { return cardNumber; }
    public String getExpiryDate() { return expiryDate; }
    public String getCardHolderName() { return cardHolderName; }
}
