package gytis.courier.domain.payment.method;

import gytis.courier.domain.payment.ProviderType;
import jakarta.validation.ValidationException;

public class CreditCard extends PaymentMethod {
    private String last4;
    private String expiryDate;
    private String cardHolderName;
    private String cardNumber;

    protected CreditCard() {}

    public static CreditCard recover(Long id, boolean saved, String token,
                                          String last4, String expiryDate, String cardHolderName) {
        CreditCard cc = new CreditCard();
        cc.id = id;
        cc.saved = saved;
        cc.token = token;
        cc.last4 = last4;
        cc.expiryDate = expiryDate;
        cc.cardHolderName = cardHolderName;
        return cc;
    }

    private CreditCard(String cardNumber, String cardHolderName, String expiryDate, boolean saveCard) {
        validateLocal(cardNumber, cardHolderName, expiryDate);
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
        this.setSaves(saveCard);
        this.last4 = cardNumber.substring(cardNumber.length() - 4);
    }

    public static CreditCard create(String cardNumber, String cardHolderName, String expiryDate, boolean saved) {
        return new CreditCard(cardNumber, cardHolderName, expiryDate, saved);
    }

    @Override
    public void clearSensitive() {
        this.cardNumber = null;
    }

    @Override
    public ProviderType providerType() { return ProviderType.CREDIT_CARD; }

    @Override
    public void validate(String cvc) {
        if (cvc == null || cvc.isBlank() || cvc.length() != 3) {
            throw new ValidationException("Invalid cvc");
        }
    }

    private void validateLocal(String cardNumber, String cardHolderName, String expiryDate) {
        if (cardHolderName == null || cardHolderName.isBlank()) throw new ValidationException("Card holder name required");
        if (expiryDate == null || !expiryDate.matches("^(0[1-9]|1[0-2])/\\d{2}$")) throw new ValidationException("Expiry date must be in MM/YY format");
        if (cardNumber == null || !cardNumber.matches("\\d{16}")) throw new ValidationException("Invalid card number");
    }

    @Override
    public void setToken(String token) {
        super.setToken(token);
        clearSensitive();
    }

    public String getToken() { return super.getToken(); }
    public String getLast4() { return last4; }
    public String getExpiryDate() { return expiryDate; }
    public String getCardHolderName() { return cardHolderName; }
    public String getCardNumber() { return cardNumber; }
}
