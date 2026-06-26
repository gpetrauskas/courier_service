package gytis.courier.application.readmodel.person;

public record BanHistoryReadModel(
        Long personId,
        boolean banned,
        String actionBy,
        String reason,
        String actionTime
) {
}
