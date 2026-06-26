package gytis.courier.application.command;

import gytis.courier.domain.task.TaskType;

import java.util.List;

public record CreateTaskCommand(
        Long adminId,
        Long courierId,
        TaskType taskType,
        List<Long> parcelIds
) {
}
