package gytis.courier.application.readmodel.person;

public record CourierReadModel(
        Long id,
        String name,
        String email,
        String phoneNumber,
        boolean isBlocked
) {
}
