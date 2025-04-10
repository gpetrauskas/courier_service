package com.example.courier.specification.person;

import com.example.courier.domain.Admin;
import com.example.courier.domain.Courier;
import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import org.springframework.data.jpa.domain.Specification;

public class PersonSpecification<T extends Person> {
    public static <T extends Person> Specification<T> hasRole(String role) {
        return (root, query, criteriaBuilder) -> {
            if (role == null || role.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            Class<?> entityClass;
            switch (role.toUpperCase()) {
                case "ADMIN":
                    entityClass = Admin.class;
                    break;
                case "COURIER":
                    entityClass = Courier.class;
                    break;
                case "USER":
                    entityClass = User.class;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid role: " + role);
            }
            return criteriaBuilder.equal(root.type(), entityClass);
        };
    }

    public static <T extends Person> Specification<T> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) ->
                userId != null ?
                        criteriaBuilder.equal(root.get("id"), userId)
                        :
                        criteriaBuilder.conjunction();
    }

    public static <T extends Person> Specification<T> hasKeyword(String searchWording) {
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

    public static <T extends Person> Specification<T> isNotDeleted() {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("isDeleted")));
    }

    public static <T extends Person> Specification<T> isNotBlocked() {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("isBlocked")));
    }

    public static <T extends Person> Specification<T> hasNoActiveTask() {
        return (((root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("hasActiveTask"))));
    }
}
