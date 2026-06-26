package gytis.courier.adapter.out.persistence.notification;

import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

public class NotificationSpecification {

    public static Specification<NotificationJpaEntity> hasKeyword(String keyword) {
        Objects.requireNonNull(keyword);

        String searchPattern = "%" + keyword.toLowerCase() + "%";

        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), searchPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("message")), searchPattern)
            );
        };
    }
}
