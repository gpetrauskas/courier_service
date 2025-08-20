package com.example.courier.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PAYPAL")
public class PayPal extends PaymentMethod {

    @Column(nullable = false)
    private String ppEmail;

    @Nullable
    public String getPpEmail() {
        return ppEmail;
    }

    public void setPpEmail(@Nullable String ppEmail) {
        this.ppEmail = ppEmail;
    }

    @Override
    public void softDelete() {
        setSaved(false);
    }
}
