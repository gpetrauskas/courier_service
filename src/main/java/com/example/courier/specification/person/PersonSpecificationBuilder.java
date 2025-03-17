package com.example.courier.specification.person;

import com.example.courier.domain.Admin;
import com.example.courier.domain.Courier;
import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import org.springframework.data.jpa.domain.Specification;

public class PersonSpecificationBuilder {

    public static Specification<Person> buildPersonSpecification(String role, String searchKeyword) {
        Specification<Person> specification = Specification.where(PersonSpecification.isNotDeleted());

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

    public static Specification<Person> buildAvailableCourierSpecification() {
        return Specification.where(PersonSpecification.isNotDeleted().and(
                PersonSpecification.hasRole("COURIER").and(PersonSpecification.hasNoActiveTask())
        ));
    }
}
