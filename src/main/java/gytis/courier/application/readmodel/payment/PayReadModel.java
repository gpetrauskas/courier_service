package gytis.courier.application.readmodel.payment;

public record PayReadModel(
        String providerType,
        String transactionId,
        boolean success,
        String failureReason,
        boolean savedMethod
) {
}
