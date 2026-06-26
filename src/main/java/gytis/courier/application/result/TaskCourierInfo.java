package gytis.courier.application.result;

public record TaskCourierInfo(
        Long taskId,
        Long courierId,
        Long taskItemId
) {
}
