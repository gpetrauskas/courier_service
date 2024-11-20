package com.example.courier.specification;

import com.example.courier.common.Role;
import com.example.courier.domain.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> hasRole(String role) {
        return (root, query, criteriaBuilder) ->
                role != null && !role.isEmpty() && Role.isValidRole(role) ?
                        criteriaBuilder.equal(root.get("role"), Role.valueOf(role.toUpperCase()))
                        :
                        criteriaBuilder.conjunction();
    }

    public static Specification<User> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) ->
                userId != null ?
                        criteriaBuilder.equal(root.get("id"), userId)
                        :
                        criteriaBuilder.conjunction();
    }

    public static Specification<User> hasKeyword(String searchWording) {
        return ((root, query, criteriaBuilder) -> {
            if (searchWording == null || searchWording.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String searchPattern = "%" + searchWording.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchPattern)
            );
        });
    }

    public static Specification<User> isNotDeleted() {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("isDeleted")));
    }
}
