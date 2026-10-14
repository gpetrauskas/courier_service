package gytis.courier.application.port.out.task;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.query.filter.AdminTaskQueryFilter;
import gytis.courier.application.readmodel.task.AdminTaskCardReadModel;
import gytis.courier.application.readmodel.task.AdminTaskStatsReadModel;
import gytis.courier.application.readmodel.task.TaskListReadModel;
import gytis.courier.application.readmodel.task.AdminTaskReadModel;
import gytis.courier.application.result.AdminTaskCardResult;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminTaskQueryPort {
    PageResult<TaskListReadModel> getAll(AdminTaskQueryFilter filter, PageQuery pageQuery);
    AdminTaskReadModel getDetailedTask(Long taskId);
    boolean existsActiveByCourierId(Long courierId);
    List<AdminTaskCardResult> getCards();
    AdminTaskStatsReadModel getStats(LocalDateTime dateTime);
}
