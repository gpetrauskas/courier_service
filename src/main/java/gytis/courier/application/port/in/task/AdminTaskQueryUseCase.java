package gytis.courier.application.port.in.task;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.query.filter.AdminTaskQueryFilter;
import gytis.courier.application.readmodel.task.AdminTaskCardReadModel;
import gytis.courier.application.readmodel.task.AdminTaskStatsReadModel;
import gytis.courier.application.readmodel.task.TaskListReadModel;
import gytis.courier.application.readmodel.task.AdminTaskReadModel;
import gytis.courier.domain.common.StatsPeriod;

public interface AdminTaskQueryUseCase {
    PageResult<TaskListReadModel> getAll(AdminTaskQueryFilter filter, PageQuery pageQuery);
    AdminTaskReadModel getDetailedTask(Long taskId);
    AdminTaskCardReadModel getCards();
    AdminTaskStatsReadModel getStats(StatsPeriod period);
}
