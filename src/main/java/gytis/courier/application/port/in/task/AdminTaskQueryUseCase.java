package gytis.courier.application.port.in.task;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.query.filter.AdminTaskQueryFilter;
import gytis.courier.application.readmodel.task.TaskListReadModel;
import gytis.courier.application.readmodel.task.AdminTaskReadModel;

public interface AdminTaskQueryUseCase {
    PageResult<TaskListReadModel> getAll(AdminTaskQueryFilter filter, PageQuery pageQuery);
    AdminTaskReadModel getDetailedTask(Long taskId);
}
