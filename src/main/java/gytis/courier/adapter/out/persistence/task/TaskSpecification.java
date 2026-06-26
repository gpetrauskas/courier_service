package gytis.courier.adapter.out.persistence.task;

import gytis.courier.application.query.filter.AdminTaskQueryFilter;
import gytis.courier.domain.task.DeliveryStatus;
import gytis.courier.domain.task.TaskType;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecification {
    public static Specification<TaskJpaEntity> withFilter(AdminTaskQueryFilter filter) {

        return Specification.where(
                courier(filter.courierId()))
                .and(taskId(filter.taskListId()))
                .and(taskType(filter.taskType()))
                .and(deliveryStatus(filter.deliveryStatus()));
    }

    private static Specification<TaskJpaEntity> courier(Long courierId) {
        return ((root, query, criteriaBuilder) ->
                courierId == null ? null : criteriaBuilder.equal(root.get("courierId"), courierId));
    }

    private static Specification<TaskJpaEntity> taskId(Long taskId) {
        return (root, query, criteriaBuilder) ->
                taskId == null ? null : criteriaBuilder.equal(root.get("id"), taskId);
    }

    private static Specification<TaskJpaEntity> taskType(TaskType type) {
        return (root, query, criteriaBuilder) ->
                type == null ? null : criteriaBuilder.equal(root.get("taskType"), type);
    }

    private static Specification<TaskJpaEntity> deliveryStatus(DeliveryStatus status) {
        return ((root, query, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("deliveryStatus"), status));
    }
}
