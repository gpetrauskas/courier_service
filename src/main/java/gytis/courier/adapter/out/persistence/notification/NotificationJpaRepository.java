package gytis.courier.adapter.out.persistence.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NotificationJpaRepository extends JpaRepository<NotificationJpaEntity, Long>, JpaSpecificationExecutor<NotificationJpaEntity> {
    //Page<NotificationProjection> findAllBy(Pageable pageable);
}
