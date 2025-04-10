package com.example.courier.specification.person;

import com.example.courier.domain.Admin;
import com.example.courier.domain.Courier;
import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import org.springframework.data.jpa.domain.Specification;

public class PersonSpecificationBuilder<T extends Person> {

    public static <T extends Person> Specification<T> buildPersonSpecification(String role, String searchKeyword) {
        Specification<T> specification = Specification.where(PersonSpecification.isNotDeleted());

        if (role != null) {
            specification = specification.and(PersonSpecification.hasRole(role));
        }
        if (searchKeyword != null) {
            specification = specification.and(PersonSpecification.hasKeyword(searchKeyword));
        }

        specification = specification.and((root, query, criteriaBuilder) ->
                root.type().in(Admin.class, Courier.class, User.class));

        return specification;
    }

    public static <T extends Person> Specification<T> buildPersonSpecificationForNotification(String role) {
        Specification<T> specification = Specification.where(PersonSpecification.<T>isNotDeleted().and(
                PersonSpecification.isNotBlocked()
        ));
        if (role != null) {
            specification = specification.and(PersonSpecification.hasRole(role));
        }

        return specification;
    }

    public static <T extends Person> Specification<T> buildAvailableCourierSpecification() {
        return Specification.where(PersonSpecification.<T>isNotDeleted().and(
                PersonSpecification.<T>hasRole("COURIER").and(PersonSpecification.hasNoActiveTask())
        ));
    }
}
