package gytis.courier.adapter.out.persistence.task;

import gytis.courier.adapter.out.persistence.task.projections.*;
import gytis.courier.domain.task.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TaskJpaRepository extends JpaRepository<TaskJpaEntity, Long>, JpaSpecificationExecutor<TaskJpaEntity> {
    @Query("SELECT DISTINCT t FROM TaskJpaEntity t " +
            "LEFT JOIN FETCH t.items i " +
            "LEFT JOIN FETCH i.notes " +
            "WHERE t.id = :id")
    Optional<TaskJpaEntity> findWithItemsAndNotes(@Param("id") Long id);

    @Query("SELECT DISTINCT t FROM TaskJpaEntity t " +
            "LEFT JOIN FETCH t.items i " +
            "WHERE t.id = :id")
    Optional<TaskJpaEntity> findWithItems(@Param("id") Long id);

    boolean existsByCourierIdAndDeliveryStatusNotIn(Long courierId, List<DeliveryStatus> statuses);


    Page<TaskListProjection> findByCourierIdAndDeliveryStatusIn(Long courierId, Set<DeliveryStatus> statuses, Pageable pageable);


    @Query("""
    SELECT
        i.id AS id,
        i.parcelStatus AS parcelStatus,
        i.deliveryMethodName AS deliveryMethodName,
        i.contents AS contents,
        p.weightName AS weight,
        p.dimensionsName AS dimensions,
        CONCAT(
            sa.detailsJpa.city, ', ',
            sa.detailsJpa.street, ' ',
            sa.detailsJpa.houseNumber,
            CASE WHEN sa.detailsJpa.flatNumber IS NOT NULL
                THEN CONCAT('/', sa.detailsJpa.flatNumber)
                ELSE ''
            END,
            ', ', sa.detailsJpa.postCode
        ) AS senderAddress,
        CONCAT(
            ra.detailsJpa.city, ', ',
            ra.detailsJpa.street, ' ',
            ra.detailsJpa.houseNumber,
            CASE WHEN ra.detailsJpa.flatNumber IS NOT NULL
            THEN CONCAT('/', ra.detailsJpa.flatNumber)
            ELSE ''
            END,
            ', ', ra.detailsJpa.postCode
        ) AS recipientAddress,
        CONCAT(
            COALESCE(sa.detailsJpa.name, ''), ' ',
            COALESCE(sa.detailsJpa.phoneNumber, '')
        ) AS senderContacts,
        CONCAT(
            COALESCE(ra.detailsJpa.name, ''), ' ',
            COALESCE(ra.detailsJpa.phoneNumber, '')
        ) AS recipientContacts
        FROM TaskItemJpaEntity i
        JOIN i.parcel p
        JOIN i.senderAddress sa
        JOIN i.recipientAddress ra
        WHERE i.task.id = :taskId AND i.task.courierId = :courierId
""")
    List<CourierTaskItemProjection> findCourierItems(@Param("taskId") Long taskId, @Param("courierId") Long courierId);

    @Query("""
    SELECT
    t.taskType,
    t.deliveryStatus,
    t.createdAt,
    t.completedAt,
    (SELECT COUNT(i) FROM TaskItemJpaEntity i WHERE i.task.id = t.id) as totalItems
    FROM TaskJpaEntity t
    WHERE t.id = :taskId AND t.courier.id = :courierId
""")
    CourierTaskHistoryProjection findByIdAndCourierId(@Param("taskId") Long taskId, @Param("courierId") Long courierId);

    @Query("""
    SELECT
        t.id AS id,
        t.taskType AS taskType,
        t.deliveryStatus AS deliveryStatus,
        t.createdAt AS createdAt,
        t.completedAt AS completedAt,
        c.id AS courierId,
        c.name AS courierName,
        c.phoneNumber AS courierPhoneNumber
    FROM TaskJpaEntity t
    left join t.courier c
    where t.id = :taskId
""")
    AdminTaskHeaderProjection findAdminTaskHeader(@Param("taskId") Long taskId);

    @Query("""
    SELECT
        i.id AS id,
        i.parcelId AS parcelId,
        i.parcelStatus AS status,
        i.deliveryMethodName AS deliveryMethodName,
        i.contents AS contents,
        p.weightName AS weight,
        p.dimensionsName AS dimensions,
        CONCAT(
            sa.detailsJpa.city, ', ',
            sa.detailsJpa.street, ' ',
            sa.detailsJpa.houseNumber,
            CASE WHEN ra.detailsJpa.flatNumber IS NOT NULL
            THEN CONCAT('/', ra.detailsJpa.flatNumber)
            ELSE ''
            END,
            ', ', sa.detailsJpa.postCode
        ) AS senderAddress,
        CONCAT(
            ra.detailsJpa.city, ', ',
            ra.detailsJpa.street, ' ',
            ra.detailsJpa.houseNumber,
            CASE WHEN ra.detailsJpa.flatNumber IS NOT NULL
            THEN CONCAT('/', ra.detailsJpa.flatNumber)
            ELSE ''
            END,
            ', ', ra.detailsJpa.postCode
        ) AS recipientAddress
    FROM TaskItemJpaEntity i
    JOIN i.parcel p
    JOIN i.senderAddress sa
    JOIN i.recipientAddress ra
    WHERE i.task.id = :taskId
""")
    List<AdminTaskItemProjection> findAdminTaskItemsProjection(@Param("taskId") Long taskId);

    @Query("""
    SELECT
        t.id AS id,
        t.courierId AS courierId,
        i.id AS itemId
    FROM TaskJpaEntity t
    JOIN t.items i
    WHERE i.parcelId = :parcelId
""")
    Optional<CourierInfoProjection> findCourierInfoByParcelId(@Param("parcelId") Long parcelId);

    CourierTaskHeaderProjection findCourierTaskHeaderByIdAndCourierId(Long taskId, Long courierId);
    TaskHeaderProjection findProjectedHeaderById(Long id);
    Page<TaskListProjection> findAllProjectedBy(Specification<TaskJpaEntity> specification, Pageable pageable);
}
