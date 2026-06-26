package gytis.courier.application.port.out.task;

import gytis.courier.application.result.TaskCourierInfo;

import java.util.Optional;

public interface TaskQueryPort {
    Optional<TaskCourierInfo> findCourierInfoByParcelId(Long parcelId);
}
