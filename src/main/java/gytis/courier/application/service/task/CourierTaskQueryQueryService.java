package gytis.courier.application.service.task;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageQueryDirection;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.in.task.CourierTaskQueryUseCase;
import gytis.courier.application.port.out.task.CourierTaskPort;
import gytis.courier.application.readmodel.task.CTaskListReadModel;
import gytis.courier.application.readmodel.task.CTaskHistoryReadModel;
import gytis.courier.application.readmodel.task.CTaskReadModel;
import gytis.courier.domain.task.DeliveryStatus;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CourierTaskQueryQueryService implements CourierTaskQueryUseCase {
    private final CourierTaskPort courierTaskPort;

    public CourierTaskQueryQueryService(CourierTaskPort courierTaskPort) {
        this.courierTaskPort = courierTaskPort;
    }

    @Override
    public PageResult<CTaskListReadModel> getMyTaskHistory(PageQuery pageQuery, Long myId) {
        Set<DeliveryStatus> statuses = Set.of(DeliveryStatus.CANCELED, DeliveryStatus.COMPLETED);
        return courierTaskPort.loadTasksByStatuses(myId, statuses, pageQuery);
    }

    @Override
    public CTaskHistoryReadModel getDetailedHistoryTask(Long taskId, Long myId) {
        return courierTaskPort.getDetailedHistoryTask(taskId, myId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    @Override
    public CTaskReadModel getCurrentTask(Long taskId, Long myId) {
        return courierTaskPort.getCurrentTask(taskId, myId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found")
        );
    }

    @Override
    public PageResult<CTaskListReadModel> getAllAssigned(Long myId) {
        PageQuery pageable = new PageQuery(0, 10, "createdAt", PageQueryDirection.ASC);
        Set<DeliveryStatus> statuses = Set.of(
                DeliveryStatus.ASSIGNED,
                DeliveryStatus.IN_PROGRESS,
                DeliveryStatus.AT_CHECKPOINT,
                DeliveryStatus.RETURNING_TO_STATION
        );

        return courierTaskPort.loadTasksByStatuses(myId, statuses, pageable);
    }
}
