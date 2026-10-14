package gytis.courier.application.readmodel.task;

public record AdminTaskStatsReadModel(
        Long createdToday,
        Long completedToday,
        Double averageDurationInMinutes
) {
}
