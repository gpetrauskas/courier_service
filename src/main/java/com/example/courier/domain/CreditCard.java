package com.example.courier.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import java.io.Serializable;

@Entity
@DiscriminatorValue("CREDIT_CARD")
public class CreditCard extends PaymentMethod implements Serializable {

    @Column
    private String token;

    @Column(nullable = false)
    private String last4;

    @Column(nullable = false)
    private String expiryDate;

    @Column(nullable = false)
    private String cardHolderName;

    @Transient
    private String cvc;

    @Transient
    private String cardNumber;

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void softDelete() {
        setSaved(false);
        this.token = null;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String maskToken() {
        if (token == null) {
            return null;
        }

        return "******" + token.substring(10 - 4);
    }
}
