package com.example.courier.specification.order;

import com.example.courier.domain.Order;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecificationBuilder {

    public static Specification<Order> buildOrderSpecification(String status, Long id) {
        Specification<Order> specification = Specification.where(null);
        if (id != null) {
            specification = specification.and(OrderSpecification.hasPersonId(id));
        }
        if (status != null) {
            specification = specification.and(OrderSpecification.hasOrderStatus(status));
        }

        return OrderSpecification.fetchPayment(specification);
    }

    public static Specification<Order> buildOrderSpecificationByTaskType(String taskType) {
        return OrderSpecification.hasParcelIsAssignedFalse()
                .and(OrderSpecification.hasParcelStatus(taskType));
    }
}
