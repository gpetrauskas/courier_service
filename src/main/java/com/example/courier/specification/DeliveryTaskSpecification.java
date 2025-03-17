package com.example.courier.specification;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.common.TaskType;
import com.example.courier.domain.DeliveryTask;
import jakarta.persistence.criteria.Join;
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

    public static Specification<DeliveryTask> ifTaskActive(Long id) {
        return ((root, query, criteriaBuilder) -> {
            Predicate idPredicate = criteriaBuilder.equal(root.get("id"), id);

            Predicate statusPredicate = criteriaBuilder.not(
                    root.get("deliveryStatus").in(DeliveryStatus.CANCELED, DeliveryStatus.COMPLETED));
            Predicate hasItems = criteriaBuilder.isNotEmpty(root.get("items"));

            return criteriaBuilder.and(idPredicate, statusPredicate, hasItems);
        });
    }

    public static Specification<DeliveryTask> canBeCanceled() {
        return ((root, query, criteriaBuilder) -> {
            Join<Object, Object> itemsJoin = root.join("items");

            Predicate itemsPredicate = criteriaBuilder.not(itemsJoin.get("status")
                    .in(ParcelStatus.PICKED_UP, ParcelStatus.DELIVERED));

            Predicate deliveryStatusPredicate = root.get("deliveryStatus")
                    .in(DeliveryStatus.IN_PROGRESS, DeliveryStatus.AT_CHECKPOINT);

            return criteriaBuilder.and(itemsPredicate, deliveryStatusPredicate);
        });
    }
}
