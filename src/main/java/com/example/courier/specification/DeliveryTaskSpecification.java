package com.example.courier.specification;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.TaskType;
import com.example.courier.domain.DeliveryTask;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class DeliveryTaskSpecification {
    public static Specification<DeliveryTask> filterTasks(
            Long courierId, TaskType tType, DeliveryStatus status, Boolean canceled) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (courierId != null) {
                predicates.add(criteriaBuilder.equal(root.get("courier").get("id"), courierId));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("deliveryStatus"), status));
            }

            if (tType != null) {
                predicates.add(criteriaBuilder.equal(root.get("taskType"), tType));
            }

            if (canceled != null) {
                if (canceled) {
                    predicates.add(criteriaBuilder.isNotNull(root.get("canceledByAdminId")));
                } else {
                    predicates.add(criteriaBuilder.isNull(root.get("canceledByAdminId")));
                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
