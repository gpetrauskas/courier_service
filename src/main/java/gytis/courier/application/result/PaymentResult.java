package gytis.courier.application.result;

import gytis.courier.domain.payment.ProviderType;

public record PaymentResult(
        ProviderType providerType,
        String transactionId,
        boolean success,
        String failureReason,
        boolean savedMethod,
        String token
) {
    public static PaymentResult success(ProviderType providerType, String txId, boolean saved, String token) {
        return new PaymentResult(providerType, txId, true, null, saved, token);
    }

    public static PaymentResult failure(ProviderType providerType, String reason, boolean saved) {
        return new PaymentResult(providerType, null, false, reason, saved, null);
    }
}
