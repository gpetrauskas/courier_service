package gytis.courier.adapter.out.persistence.order;

import gytis.courier.application.query.filter.OrderQuery;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecificationBuilder {
    public static Specification<OrderJpaEntity> forUser(OrderQuery query) {
        Specification<OrderJpaEntity> specification = Specification.unrestricted();

        specification = specification.and(OrderSpecification.hasUserId(query.userId()));

        if (query.orderStatus() != null) {
            specification = specification.and(OrderSpecification.hasOrderStatus(query.orderStatus().name()));
        }

        return specification;
    }


    public static Specification<OrderJpaEntity> forAdmin(OrderQuery query) {
        Specification<OrderJpaEntity> specification = Specification.unrestricted();


        if (query.userId() != null) {
            specification = specification.and(OrderSpecification.hasUserId(query.userId()));
        }

        if (query.orderId() != null) {
            specification = specification.and(OrderSpecification.hasId(query.orderId()));
        }

        if (query.orderStatus() != null) {
            specification = specification.and(OrderSpecification.hasOrderStatus(query.orderStatus().name()));
        }

        return specification;
    }
}
