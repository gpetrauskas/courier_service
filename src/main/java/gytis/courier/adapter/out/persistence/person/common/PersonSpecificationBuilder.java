package gytis.courier.adapter.out.persistence.person.common;

import gytis.courier.adapter.out.persistence.person.admin.AdminJpaEntity;
import gytis.courier.adapter.out.persistence.person.courier.CourierJpaEntity;
import gytis.courier.adapter.out.persistence.person.user.UserJpaEntity;
import org.springframework.data.jpa.domain.Specification;

public class PersonSpecificationBuilder<T extends PersonJpaEntity> {

    public static <T extends PersonJpaEntity> Specification<T> buildPersonSpecification(String role, String searchKeyword) {
        Specification<T> specification = Specification.where(PersonSpecification.isNotDeleted());

        if (role != null) {
            specification = specification.and(PersonSpecification.hasRole(role));
        }
        if (searchKeyword != null) {
            specification = specification.and(PersonSpecification.hasKeyword(searchKeyword));
        }

        specification = specification.and((root, query, criteriaBuilder) ->
                root.type().in(AdminJpaEntity.class, CourierJpaEntity.class, UserJpaEntity.class));

        return specification;
    }

    public static <T extends PersonJpaEntity> Specification<T> buildPersonSpecificationForNotification(String role) {
        Specification<T> specification = Specification.where(PersonSpecification.<T>isNotDeleted().and(
                PersonSpecification.isNotBlocked()
        ));
        if (role != null) {
            specification = specification.and(PersonSpecification.hasRole(role));
        }

        return specification;
    }

    public static <T extends PersonJpaEntity> Specification<T> buildAvailableCourierSpecification() {
        return Specification.where(PersonSpecification.<T>isNotDeleted().and(
                PersonSpecification.<T>hasRole("COURIER").and(PersonSpecification.hasNoActiveTask())
        ));
    }
}
