package gytis.courier.adapter.out.persistence.notification;

import gytis.courier.application.query.filter.AdminNotificationQuery;
import org.springframework.data.jpa.domain.Specification;

public class NotificationSpecificationBuilder {
    public static Specification<NotificationJpaEntity> from(AdminNotificationQuery query) {
        if (query == null) return Specification.where(null);

        Specification<NotificationJpaEntity> specification = Specification.where(null);

        if (query.keyword() != null) {
            specification = specification.and(NotificationSpecification.hasKeyword(query.keyword()));
        }

        return specification;
    };
}
