package gytis.courier.adapter.out.persistence.person.admin;

import gytis.courier.adapter.out.persistence.person.projection.AdminInfoProjection;
import gytis.courier.adapter.out.persistence.person.projection.AdminViewAdminProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AdminJpaRepository extends JpaRepository<AdminJpaEntity, Long> {
    @Query("""
    SELECT a.name AS name,
           a.email AS email,
           (SELECT COUNT(t) FROM TaskJpaEntity t WHERE t.createdByAdminId = :id) AS createdTasks
    FROM AdminJpaEntity a
    WHERE a.id = :id
    """)
    Optional<AdminInfoProjection> getAdminInfo(Long id);

    @Query("SELECT a.id FROM AdminJpaEntity a WHERE a.blocked = false AND a.deleted = false")
    List<Long> findAllActiveIds(Pageable pageable);

    @Query("""
    SELECT a.id AS id,
           a.name AS name,
           a.email AS email,
           a.phoneNumber As phoneNumber,
           a.blocked AS blocked,
           a.deleted AS deleted,
           a.deletedDate AS deletedDate,
           COUNT(t.id) AS createdTasks
    FROM AdminJpaEntity a
    LEFT JOIN TaskJpaEntity t ON t.createdByAdminId = a.id
    WHERE a.id = :id
    GROUP BY a.id
""")
    Optional<AdminViewAdminProjection> getAdminDetailedById(Long id);
}
