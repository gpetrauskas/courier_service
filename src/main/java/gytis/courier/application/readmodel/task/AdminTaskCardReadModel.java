package gytis.courier.application.readmodel.task;

public record AdminTaskCardReadModel(
        Long pickingUp,
        Long delivering,
        Long pickedUp,
        Long delivered,
        Long returning,
        Long awaitingConfirmation
) {
}
