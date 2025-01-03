package com.example.courier.specification;

import com.example.courier.common.PackageStatus;
import com.example.courier.domain.Order;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {
    public static Specification<Order> hasPackageStatus(String packageStatus) {
        return (root, query, criteriaBuilder) -> {
            if (packageStatus != null && !packageStatus.isEmpty() && PackageStatus.isValidStatus(packageStatus)) {
                Join<Object, Object> packageJoin = root.join("packageDetails");
                return criteriaBuilder.equal(packageJoin.get("status"), PackageStatus.valueOf(packageStatus.toUpperCase()));
            }
            return criteriaBuilder.conjunction();
        };
    }
}
