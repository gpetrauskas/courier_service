package gytis.courier.adapter.out.persistence.person.common;

import gytis.courier.adapter.out.persistence.person.projection.AdminPersonListProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PersonJpaRepository extends JpaRepository<PersonJpaEntity, Long>, JpaSpecificationExecutor<PersonJpaEntity> {
    Optional<PersonJpaEntity> findPersonByEmail(String email);
    Page<AdminPersonListProjection> findBy(Specification<PersonJpaEntity> specification, Pageable pageable);
    boolean existsByEmail(String email);

    @Query("""
    SELECT
        CASE
            WHEN TYPE(p) = AdminJpaEntity THEN 'ADMIN'
            WHEN TYPE(p) = CourierJpaEntity THEN 'COURIER'
            WHEN TYPE(p) = UserJpaEntity THEN 'USER'
        END
    FROM PersonJpaEntity p
    WHERE p.id = :id
""")
    Optional<String> findRoleById(Long id);
}
