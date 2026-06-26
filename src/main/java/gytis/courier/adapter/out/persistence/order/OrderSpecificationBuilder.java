package gytis.courier.adapter.out.persistence.order;

import gytis.courier.application.query.filter.OrderQuery;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecificationBuilder {
    public static Specification<OrderJpaEntity> from(OrderQuery query) {
        Specification<OrderJpaEntity> specification = Specification.where(null);

        if (query.id() != null) {
            specification = specification.and(OrderSpecification.hasId(query.id()));
        }

        if (query.orderStatus() != null) {
            specification = specification.and(OrderSpecification.hasOrderStatus(query.orderStatus().name()));
        }

        return specification;
    }
}
