package gytis.courier.adapter.out.persistence.order;

import gytis.courier.adapter.out.persistence.parcel.ParcelJpaEntity;
import gytis.courier.domain.order.OrderStatus;
import gytis.courier.domain.order.ParcelStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;
import java.util.Set;

public class OrderSpecification {
    private static final String PARCEL = "parcel";
    private static final String STATUS = "status";
    private static final String IS_ASSIGNED = "assigned";
    private static final String USER_ID = "userId";
    private static final String ID = "id";

    public static Specification<OrderJpaEntity> hasParcelStatusIn(Set<ParcelStatus> statuses) {
        Objects.requireNonNull(statuses);
        if (statuses.isEmpty()) {
            throw new IllegalArgumentException("Statusses cannot be empty");
        }

        return (root, query, criteriaBuilder) ->
                getOrJoinParcel(root).get(STATUS).in(statuses);
    }

    public static Specification<OrderJpaEntity> hasOrderStatus(String orderStatus) {
        OrderStatus.isValidStatus(orderStatus);
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(STATUS), orderStatus));
    }

    public static Specification<OrderJpaEntity> hasId(Long id) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(ID), id));
    }

    public static Specification<OrderJpaEntity> hasParcelIsAssignedFalse() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(getOrJoinParcel(root).get(IS_ASSIGNED));
    }

    public static Specification<OrderJpaEntity> hasUserId(Long userId) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(USER_ID), userId));
    }

    @SuppressWarnings("unchecked")
    private static Join<OrderJpaEntity, ParcelJpaEntity> getOrJoinParcel(Root<OrderJpaEntity> root) {
        return (Join<OrderJpaEntity, ParcelJpaEntity>) root.getJoins().stream()
                .filter(j -> j.getAttribute().getName().equals(PARCEL))
                .findFirst()
                .orElseGet(() -> root.join(PARCEL));
    }
}