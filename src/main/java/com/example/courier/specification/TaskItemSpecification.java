package com.example.courier.specification;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.DeliveryTaskItem;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class TaskItemSpecification {

    public static Specification<DeliveryTaskItem> isActiveTaskItem(Long id) {
        return (root, query, criteriaBuilder) -> {
            Predicate idPredicate = criteriaBuilder.equal(root.get("id"), id);
            Predicate statusPredicate = criteriaBuilder.not(root.get("status").in(ParcelStatus.getStatusesPreventingRemoval()));
            Join<Object, Object> taskJoin = root.join("task");
            Predicate taskStatusPredicate = criteriaBuilder.not(taskJoin.get("deliveryStatus").in(DeliveryStatus.historicalStatuses()));
            return criteriaBuilder.and(idPredicate, statusPredicate, taskStatusPredicate);
        };
    }
}
