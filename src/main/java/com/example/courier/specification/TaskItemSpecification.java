package com.example.courier.specification;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.TaskItem;
import org.springframework.data.jpa.domain.Specification;

public class TaskItemSpecification {

    public static Specification<TaskItem> isActiveTaskItem(Long id) {
        return Specification.where(hasId(id))
                .and(isNotCanceledOrRemoved())
                .and(isTaskNotHistorical());
    }

    public static Specification<TaskItem> hasId(Long id) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), id));
    }

    public static Specification<TaskItem> isNotCanceledOrRemoved() {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.not(root.get("status").in(ParcelStatus.getStatusesPreventingRemoval()));
    }

    public static Specification<TaskItem> isTaskNotHistorical() {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.not(root.join("task").get("deliveryStatus").in(DeliveryStatus.historicalStatuses()));
    }

}
