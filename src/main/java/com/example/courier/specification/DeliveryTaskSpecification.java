package com.example.courier.specification;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.TaskType;
import com.example.courier.domain.DeliveryTask;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class DeliveryTaskSpecification {
    public static Specification<DeliveryTask> filterTasks(
            Long courierId, Long taskListId, TaskType tType, DeliveryStatus status, Boolean canceled) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = Stream.of(
                    courierId != null ? criteriaBuilder.equal(root.get("courier").get("id"), courierId) : null,
                    taskListId != null ? criteriaBuilder.equal(root.get("id"), taskListId) : null,
                    status != null ? criteriaBuilder.equal(root.get("deliveryStatus"), status) : null,
                    tType != null ? criteriaBuilder.equal(root.get("taskType"), tType) : null,
                    canceled != null
                            ? (canceled ? criteriaBuilder.isNotNull(root.get("canceledByAdminId"))
                                : criteriaBuilder.isNull(root.get("canceledByAdminId")))
                            : null
            ).filter(Objects::nonNull).toList();
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
