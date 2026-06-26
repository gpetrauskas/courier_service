package gytis.courier.adapter.out.persistence.order;

import gytis.courier.adapter.out.persistence.order.projection.OrderForTaskProjection;
import gytis.courier.adapter.out.persistence.order.projection.OrderDetailProjection;
import gytis.courier.adapter.out.persistence.order.projection.OrderListProjection;
import gytis.courier.adapter.out.persistence.order.projection.TaskItemCreationProjection;
import gytis.courier.adapter.out.persistence.task.OrderAddressIdsProjection;
import gytis.courier.domain.order.ParcelStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Long>, JpaSpecificationExecutor<OrderJpaEntity> {
    @EntityGraph(attributePaths = {"parcel"})
    Optional<OrderJpaEntity> findWithParcelById(Long id);

    @EntityGraph(attributePaths = { "parcel", "senderAddress", "recipientAddress" })
    Optional<OrderJpaEntity> findWithParcelAndAddressesById(Long orderId);

    List<OrderJpaEntity> findAllByParcelIdIn(List<Long> ids);


    // helpers
    @Query("SELECT COUNT(o) FROM OrderJpaEntity o WHERE o.userId = :userId and o.status = 'CONFIRMED'")
    long countActiveOrders(@Param("userId") Long userId);

    boolean existsByIdAndUserId(Long orderId, Long userId);


    // for query
    OrderAddressIdsProjection findByParcelId(Long parcelId);
    Optional<OrderDetailProjection> findOrderDetailById(Long orderId);
    Page<OrderListProjection> findByUserId(Pageable pageable, Long userId);
    Optional<OrderDetailProjection> findOrderDetailByIdAndUserId(Long orderId, Long userId);

    @Query("SELECT " +
            "o.deliveryMethodName AS deliveryMethodName, " +
            "o.senderAddress.id AS senderAddressId, " +
            "o.recipientAddress.id AS recipientAddressId, " +
            "p.id AS parcelId, " +
            "p.contents AS contents, " +
            "p.status AS parcelStatus " +
            "FROM OrderJpaEntity o " +
            "JOIN o.parcel p " +
            "WHERE p.id IN :parcelIds AND p.assigned = false")
    List<TaskItemCreationProjection> findAllForTaskItemCreation(@Param("parcelIds") List<Long> parcelIds);

    @Query("""
        SELECT
        o.id AS id,
        o.createDate AS createDate,
        o.deliveryMethodName AS deliveryMethodName,
        a.detailsJpa.name AS name,
        a.detailsJpa.phoneNumber AS phoneNumber,
        a.detailsJpa.street AS street,
        a.detailsJpa.houseNumber AS houseNumber,
        a.detailsJpa.flatNumber AS flatNumber,
        a.detailsJpa.city AS city,
        a.detailsJpa.postCode as postCode,
        p.id AS parcelId,
        p.status AS parcelStatus,
        p.weightName as weight,
        p.dimensionsName AS dimensions,
        p.contents AS contents,
        p.failuresCount AS failuresCount
        FROM OrderJpaEntity o
        JOIN o.senderAddress AS a
        JOIN o.parcel AS p
        WHERE p.status IN :statuses
        AND p.assigned = false
""")
    Page<OrderForTaskProjection> findAllForPickup(Set<ParcelStatus> statuses, Pageable pageable);


    @Query("""
        SELECT
        o.id AS id,
        o.createDate AS createDate,
        o.deliveryMethodName AS deliveryMethodName,
        a.detailsJpa.name AS name,
        a.detailsJpa.phoneNumber AS phoneNumber,
        a.detailsJpa.street AS street,
        a.detailsJpa.houseNumber AS houseNumber,
        a.detailsJpa.flatNumber AS flatNumber,
        a.detailsJpa.city AS city,
        a.detailsJpa.postCode as postCode,
        p.id AS parcelId,
        p.status AS parcelStatus,
        p.weightName as weight,
        p.dimensionsName AS dimensions,
        p.contents AS contents,
        p.failuresCount AS failuresCount
        FROM OrderJpaEntity o
        JOIN o.recipientAddress AS a
        JOIN o.parcel AS p
        WHERE p.status IN :statuses
        AND p.assigned = false
""")
    Page<OrderForTaskProjection> findAllForDelivery(Set<ParcelStatus> statuses, Pageable pageable);


    //Page<OrderForTaskProjection> findAllForTaskBy(Specification<OrderJpaEntity> spec, Pageable pageable);

    // for command
    @EntityGraph(attributePaths = "parcel")
    Optional<OrderJpaEntity> findWithParcelByIdAndUserId(Long orderId, Long userId);
}
