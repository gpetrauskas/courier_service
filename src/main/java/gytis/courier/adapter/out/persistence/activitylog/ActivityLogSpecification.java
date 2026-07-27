package gytis.courier.adapter.out.persistence.activitylog;

import gytis.courier.application.query.filter.ActivityLogQuery;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ActivityLogSpecification {
    public static Specification<ActivityLogJpaEntity> from(ActivityLogQuery logQuery) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if (logQuery.role() != null && !logQuery.role().isBlank()) list.add(criteriaBuilder.equal(root.get("role"), logQuery.role()));
            if (logQuery.keyword() != null && !logQuery.keyword().isBlank()) list.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("userEmail")), "%" + logQuery.keyword().toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("action")), "%" + logQuery.keyword().toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + logQuery.keyword().toLowerCase() + "%")
            ));

            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
    }
}




