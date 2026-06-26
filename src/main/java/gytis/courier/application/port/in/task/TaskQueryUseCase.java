package gytis.courier.application.port.in.task;

import gytis.courier.application.result.TaskCourierInfo;

import java.util.Optional;

public interface TaskQueryUseCase {
    Optional<TaskCourierInfo> findCourierInfoByParcelId(Long parcelId);
}
