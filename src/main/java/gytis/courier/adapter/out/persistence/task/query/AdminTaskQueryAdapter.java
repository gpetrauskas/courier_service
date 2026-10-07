package gytis.courier.adapter.out.persistence.task.query;

import gytis.courier.adapter.out.persistence.common.PageResultMapper;
import gytis.courier.adapter.out.persistence.common.PageableFactory;
import gytis.courier.adapter.out.persistence.task.TaskJpaEntity;
import gytis.courier.adapter.out.persistence.task.TaskJpaRepository;
import gytis.courier.adapter.out.persistence.task.TaskSpecification;
import gytis.courier.adapter.out.persistence.task.projections.AdminTaskHeaderProjection;
import gytis.courier.adapter.out.persistence.task.projections.TaskListProjection;
import gytis.courier.adapter.out.persistence.task.projections.TaskReadModelMapper;
import gytis.courier.adapter.out.persistence.task.projections.TaskResultMapper;
import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.out.task.AdminTaskQueryPort;
import gytis.courier.application.query.filter.AdminTaskQueryFilter;
import gytis.courier.application.readmodel.task.AdminTaskCardReadModel;
import gytis.courier.application.readmodel.task.TaskListReadModel;
import gytis.courier.application.readmodel.task.AdminTaskItemReadModel;
import gytis.courier.application.readmodel.task.AdminTaskReadModel;
import gytis.courier.application.result.AdminTaskCardResult;
import gytis.courier.domain.task.DeliveryStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminTaskQueryAdapter implements AdminTaskQueryPort {
    private final TaskJpaRepository repository;
    private final TaskReadModelMapper mapper;
    private final TaskResultMapper resultMapper;

    public AdminTaskQueryAdapter(TaskJpaRepository repository, TaskReadModelMapper mapper, TaskResultMapper resultMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.resultMapper = resultMapper;
    }

    @Override
    public boolean existsActiveByCourierId(Long courierId) {
        return repository.existsByCourierIdAndDeliveryStatusNotIn(
                courierId,
                List.of(DeliveryStatus.COMPLETED, DeliveryStatus.CANCELED)
        );
    }

    @Override
    public List<AdminTaskCardResult> getCards() {
        return repository.findCards().stream()
                .map(resultMapper::toCardResult)
                .toList();
    }

    @Override
    public PageResult<TaskListReadModel> getAll(AdminTaskQueryFilter filter, PageQuery pageQuery) {
        Pageable pageable = PageableFactory.from(pageQuery);
        Specification<TaskJpaEntity> specification = TaskSpecification.withFilter(filter);

        return PageResultMapper.from(
                repository.findBy(
                        specification,
                        q -> q.as(TaskListProjection.class).page(pageable)),
                mapper::toTaskListReadModel
        );
    }

    @Override
    public AdminTaskReadModel getDetailedTask(Long taskId) {
        AdminTaskHeaderProjection h = repository.findAdminTaskHeader(taskId);
        List<AdminTaskItemReadModel> i = repository.findAdminTaskItemsProjection(taskId).stream()
                .map(mapper::toItemAdmin)
                .toList();

        return mapper.toAdminDetailed(h, i);
    }
}
