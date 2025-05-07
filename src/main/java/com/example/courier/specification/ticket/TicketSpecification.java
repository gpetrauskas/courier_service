package com.example.courier.specification.ticket;

import com.example.courier.common.TicketStatus;
import com.example.courier.domain.Ticket;
import org.springframework.data.jpa.domain.Specification;

public class TicketSpecification {
    public static Specification<Ticket> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), TicketStatus.valueOf(status.toUpperCase()));
        };
    }

    public static Specification<Ticket> createdBy(Long personId) {
        return (root, query, criteriaBuilder) -> {
            if (personId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("createdBy").get("id"), personId);
        };
    }
}
