package gytis.courier.adapter.out.persistence.person.courier;

import gytis.courier.adapter.out.persistence.person.projection.AdminViewCourierProjection;
import gytis.courier.adapter.out.persistence.person.projection.CourierInfoProjection;
import gytis.courier.adapter.out.persistence.person.projection.CourierProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CourierJpaRepository extends JpaRepository<CourierJpaEntity, Long> {
    List<CourierProjection> findByHasActiveTaskFalse();
    Long countByHasActiveTaskFalse();

    @Query("""
    SELECT c.name AS name,
           c.email AS email,
           c.hasActiveTask AS hasActiveTask
    FROM CourierJpaEntity c
    WHERE c.id = :id
    """)
    Optional<CourierInfoProjection> getCourierInfo(Long id);

    @Query("SELECT c.id FROM CourierJpaEntity c WHERE c.blocked = false AND c.deleted = false")
    List<Long> findAllActiveIds(Pageable pageable);

    @Query("""
    SELECT c.id AS id,
           c.name AS name,
           c.email AS email,
           c.phoneNumber AS phoneNumber,
           c.blocked AS blocked,
           c.deleted AS deleted,
           c.deletedDate AS deletedDate,
           c.hasActiveTask AS activeTask,
           COUNT(t.id) AS completedDeliveries
    FROM CourierJpaEntity c
    LEFT JOIN TaskJpaEntity t ON t.courierId = c.id AND t.deliveryStatus = 'COMPLETED'
    WHERE c.id = :id
    """)
    Optional<AdminViewCourierProjection> getAdminDetailedById(Long id);
}
