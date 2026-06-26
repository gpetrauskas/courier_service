package gytis.courier.application.port.out.task;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.readmodel.task.CTaskListReadModel;
import gytis.courier.application.readmodel.task.CTaskHistoryReadModel;
import gytis.courier.application.readmodel.task.CTaskReadModel;
import gytis.courier.domain.task.DeliveryStatus;

import java.util.Optional;
import java.util.Set;

public interface CourierTaskPort {
    Optional<CTaskReadModel> getCurrentTask(Long taskId, Long courierId);
    PageResult<CTaskListReadModel> loadTasksByStatuses(Long courierId, Set<DeliveryStatus> statuses, PageQuery pageQuery);
    Optional<CTaskHistoryReadModel> getDetailedHistoryTask(Long taskId, Long courierId);
}
