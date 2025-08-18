package com.example.courier.exception;

import com.example.courier.common.PaymentAttemptStatus;
import com.example.courier.common.ProviderType;

public class PaymentFailedException extends RuntimeException {
    private final ProviderType type;
    private final PaymentAttemptStatus status;
    private final String txId;

    public PaymentFailedException(String message, ProviderType type, PaymentAttemptStatus status,
                                  String txId) {
        super(message);
        this.type = type;
        this.status = status;
        this.txId = txId;

    }

    public ProviderType getType() {
        return type;
    }

    public PaymentAttemptStatus getStatus() {
        return status;
    }

    public String getTxId() {
        return txId;
    }
}
