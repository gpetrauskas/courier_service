package com.example.courier.specification.order;

import com.example.courier.common.OrderStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.Order;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {
    public static Specification<Order> hasParcelStatus(String parcelStatus) {
        if (parcelStatus == null || parcelStatus.isBlank()) {
            throw new IllegalArgumentException("Status cannot be null or blank");
        }
        ParcelStatus.validateStatus(parcelStatus);
        return ((root, query, criteriaBuilder) -> {
            Join<Object, Object> parcelJoin = root.join("parcelDetails");
            return criteriaBuilder.equal(parcelJoin.get("status"), ParcelStatus.valueOf(parcelStatus.toUpperCase()));
        });
    }

    public static Specification<Order> hasOrderStatus(String orderStatus) {
        OrderStatus.isValidStatus(orderStatus);
        return ((root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("status"), orderStatus));
    }

    public static Specification<Order> hasPersonId(Long personId) {
        return ((root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("id"), personId));
    }

    public static Specification<Order> hasParcelIsAssignedFalse() {
        return ((root, query, criteriaBuilder) -> {
            Join<Object, Object> parcelJoin = root.join("parcelDetails");
            return criteriaBuilder.isFalse(parcelJoin.get("isAssigned"));
        });
    }
}
