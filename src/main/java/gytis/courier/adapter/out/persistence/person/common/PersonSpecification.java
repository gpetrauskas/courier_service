package gytis.courier.adapter.out.persistence.person.common;

import gytis.courier.adapter.out.persistence.person.admin.AdminJpaEntity;
import gytis.courier.adapter.out.persistence.person.courier.CourierJpaEntity;
import gytis.courier.adapter.out.persistence.person.user.UserJpaEntity;
import org.springframework.data.jpa.domain.Specification;

public class PersonSpecification<T extends PersonJpaEntity> {
    public static <T extends PersonJpaEntity> Specification<T> hasRole(String role) {
        return (root, query, criteriaBuilder) -> {
            if (role == null || role.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            Class<?> entityClass;
            switch (role.toUpperCase()) {
                case "ADMIN":
                    entityClass = AdminJpaEntity.class;
                    break;
                case "COURIER":
                    entityClass = CourierJpaEntity.class;
                    break;
                case "USER":
                    entityClass = UserJpaEntity.class;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid role: " + role);
            }
            return criteriaBuilder.equal(root.type(), entityClass);
        };
    }

    public static <T extends PersonJpaEntity> Specification<T> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) ->
                userId != null ?
                        criteriaBuilder.equal(root.get("id"), userId)
                        :
                        criteriaBuilder.conjunction();
    }

    public static <T extends PersonJpaEntity> Specification<T> hasKeyword(String searchWording) {
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

    public static <T extends PersonJpaEntity> Specification<T> isNotDeleted() {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("deleted")));
    }

    public static <T extends PersonJpaEntity> Specification<T> isNotBlocked() {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("blocked")));
    }

    public static <T extends PersonJpaEntity> Specification<T> hasNoActiveTask() {
        return (((root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("hasActiveTask"))));
    }
}
