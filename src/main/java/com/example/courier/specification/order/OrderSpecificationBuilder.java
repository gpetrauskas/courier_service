package com.example.courier.specification.order;

import com.example.courier.domain.Order;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecificationBuilder {

    public static Specification<Order> buildOrderSpecification(Long userId, String status) {
        Specification<Order> specification = Specification.where(null);
        if (userId != null) {
            specification = specification.and(OrderSpecification.hasPersonId(userId));
        }
        if (status != null) {
            specification = specification.and(OrderSpecification.hasParcelStatus(status));
        }

        return specification;
    }
}
