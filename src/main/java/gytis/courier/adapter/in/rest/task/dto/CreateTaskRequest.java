package gytis.courier.adapter.in.rest.task.dto;

import gytis.courier.domain.task.TaskType;

import java.util.List;

public record CreateTaskRequest(
        Long courierId,
        TaskType type,
        List<Long> parcelIds
) {
}
