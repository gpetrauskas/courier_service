package gytis.courier.adapter.in.rest.task.dto;

public record AdminTaskFilterRequest(
        int page,
        int size,
        Long courierId,
        Long taskListId,
        String taskType,
        String deliveryStatus,
        String sortBy,
        String direction
) {
}
