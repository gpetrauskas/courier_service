package gytis.courier.adapter.out.persistence.task.query;

import gytis.courier.adapter.out.persistence.common.PageResultMapper;
import gytis.courier.adapter.out.persistence.common.PageableFactory;
import gytis.courier.adapter.out.persistence.task.TaskJpaRepository;
import gytis.courier.adapter.out.persistence.task.projections.TaskReadModelMapper;
import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.out.task.CourierTaskPort;
import gytis.courier.application.readmodel.task.CTaskListReadModel;
import gytis.courier.application.readmodel.task.CTaskHistoryReadModel;
import gytis.courier.application.readmodel.task.CTaskItemReadModel;
import gytis.courier.application.readmodel.task.CTaskReadModel;
import gytis.courier.domain.task.DeliveryStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class CourierTaskQueryAdapter implements CourierTaskPort {
    private final TaskJpaRepository repository;
    private final TaskReadModelMapper mapper;

    public CourierTaskQueryAdapter(TaskJpaRepository repository, TaskReadModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<CTaskReadModel> getCurrentTask(Long taskId, Long courierId) {
        var courierTaskHeader = repository.findCourierTaskHeaderByIdAndCourierId(taskId, courierId);
        if (courierTaskHeader == null) return Optional.empty();

        List<CTaskItemReadModel> courierItems = repository.findCourierItems(taskId, courierId).stream()
                .map(i -> mapper.toCourierItem(i, courierTaskHeader.getTaskType()))
                .toList();

        return Optional.of(
                mapper.toCourierCurrentDetailed(
                        courierTaskHeader,
                        courierItems
                )
        );
    }

    //history list
    @Override
    public PageResult<CTaskListReadModel> loadTasksByStatuses(Long courierId, Set<DeliveryStatus> statuses, PageQuery pageQuery) {
        Pageable pageable = PageableFactory.from(pageQuery);
        var projections =  repository.findByCourierIdAndDeliveryStatusIn(
                courierId,
                statuses,
                pageable
        );

        return PageResultMapper.from(
                projections,
                mapper::toListReadModel
        );
    }

    @Override
    public Optional<CTaskHistoryReadModel> getDetailedHistoryTask(Long taskId, Long courierId) {
        return Optional.ofNullable(repository.findByIdAndCourierId(taskId, courierId))
                .map(mapper::toCourierHistory);
    }
}
