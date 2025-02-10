package com.example.courier.specification;

import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.Order;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {
    public static Specification<Order> hasParcelStatus(String parcelStatus) {
        return (root, query, criteriaBuilder) -> {
            if (parcelStatus != null && !parcelStatus.isEmpty() && ParcelStatus.isValidStatus(parcelStatus)) {
                Join<Object, Object> parcelJoin = root.join("parcelDetails");
                return criteriaBuilder.equal(parcelJoin.get("status"), ParcelStatus.valueOf(parcelStatus.toUpperCase()));
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Order> hasParcelIsAssignedFalse() {
        return ((root, query, criteriaBuilder) -> {
            Join<Object, Object> parcelJoin = root.join("parcelDetails");
            return criteriaBuilder.isFalse(parcelJoin.get("isAssigned"));
        });
    }
}
