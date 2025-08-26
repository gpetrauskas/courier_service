package com.example.courier.dto;

import com.example.courier.domain.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonIgnore;


public final class OneTimeCard extends PaymentMethod {
    private final String cardNumber;
    private final String expiryDate;
    private final String cardHolderName;

    public OneTimeCard(String cardNumber, String expiryDate, String cardHolderName) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cardHolderName = cardHolderName;
    }

    @JsonIgnore
    public String getCardNumber() {
        return cardNumber;
    }
    public String getExpiryDate() {
        return expiryDate;
    }
    public String getCardHolderName() {
        return cardHolderName;
    }
    public String getLast4() {
        return cardNumber != null && cardNumber.length() >= 4
                ? cardNumber.substring(cardNumber.length() - 4) : "";
    }

    @Override
    public String toString() {
        return getLast4();
    }

    @Override
    public void softDelete() {
        // class is nowhere saved
    }
}
