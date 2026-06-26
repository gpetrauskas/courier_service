package gytis.courier.application.port.in.task;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.readmodel.task.CTaskListReadModel;
import gytis.courier.application.readmodel.task.CTaskHistoryReadModel;
import gytis.courier.application.readmodel.task.CTaskReadModel;

public interface CourierTaskQueryUseCase {
    CTaskReadModel getCurrentTask(Long taskId, Long courierId);
    PageResult<CTaskListReadModel> getAllAssigned(Long courierId);
    PageResult<CTaskListReadModel> getMyTaskHistory(PageQuery pageQuery, Long courierId);
    CTaskHistoryReadModel getDetailedHistoryTask(Long taskId, Long courierId);

}
