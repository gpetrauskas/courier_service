package gytis.courier.application.service.task;

import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.in.task.AdminTaskQueryUseCase;
import gytis.courier.application.port.out.task.AdminTaskQueryPort;
import gytis.courier.application.query.filter.AdminTaskQueryFilter;
import gytis.courier.application.readmodel.task.AdminTaskCardReadModel;
import gytis.courier.application.readmodel.task.TaskListReadModel;
import gytis.courier.application.readmodel.task.AdminTaskReadModel;
import gytis.courier.application.result.AdminTaskCardResult;
import gytis.courier.domain.task.DeliveryStatus;
import gytis.courier.domain.task.TaskType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

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

    @Override
    public AdminTaskCardReadModel getCards() {
        List<AdminTaskCardResult> cards = port.getCards();
        return new AdminTaskCardReadModel(
                countTasks(cards, p -> p.taskType() == TaskType.PICKUP && p.deliveryStatus() == DeliveryStatus.IN_PROGRESS),
                countTasks(cards, p -> p.taskType() == TaskType.DELIVERY && p.deliveryStatus() == DeliveryStatus.IN_PROGRESS),
                countTasks(cards, p -> p.taskType() == TaskType.PICKUP && p.deliveryStatus() == DeliveryStatus.COMPLETED),
                countTasks(cards, p -> p.taskType() == TaskType.DELIVERY && p.deliveryStatus() == DeliveryStatus.COMPLETED),
                countTasks(cards, p -> p.deliveryStatus() == DeliveryStatus.RETURNING_TO_STATION),
                countTasks(cards, p -> p.deliveryStatus() == DeliveryStatus.AT_CHECKPOINT)
                );
    }

    private long countTasks(List<AdminTaskCardResult> cardResults, Predicate<AdminTaskCardResult> resultPredicate) {
        return cardResults.stream()
                .filter(resultPredicate)
                .mapToLong(AdminTaskCardResult::count)
                .sum();
        }

}
