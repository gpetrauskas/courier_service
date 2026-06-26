package gytis.courier.application.service.task;

import gytis.courier.application.port.in.task.TaskQueryUseCase;
import gytis.courier.application.port.out.task.TaskQueryPort;
import gytis.courier.application.result.TaskCourierInfo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaskQueryService implements TaskQueryUseCase {
    private final TaskQueryPort port;

    public TaskQueryService(TaskQueryPort port) {
        this.port = port;
    }

    @Override
    public Optional<TaskCourierInfo> findCourierInfoByParcelId(Long parcelId) {
        return port.findCourierInfoByParcelId(parcelId);
    }
}
