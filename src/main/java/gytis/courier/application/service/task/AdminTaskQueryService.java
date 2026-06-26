package gytis.courier.application.service.task;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.in.task.AdminTaskQueryUseCase;
import gytis.courier.application.port.out.task.AdminTaskQueryPort;
import gytis.courier.application.query.filter.AdminTaskQueryFilter;
import gytis.courier.application.readmodel.task.TaskListReadModel;
import gytis.courier.application.readmodel.task.AdminTaskReadModel;
import org.springframework.stereotype.Service;

@Service
public class AdminTaskQueryService implements AdminTaskQueryUseCase {
    private final AdminTaskQueryPort port;

    public AdminTaskQueryService(AdminTaskQueryPort port) {
        this.port = port;
    }

    @Override
    public PageResult<TaskListReadModel> getAll(AdminTaskQueryFilter filter, PageQuery pageQuery) {
        return port.getAll(filter, pageQuery);
    }

    @Override
    public AdminTaskReadModel getDetailedTask(Long taskId) {
        return port.getDetailedTask(taskId);
    }
}
