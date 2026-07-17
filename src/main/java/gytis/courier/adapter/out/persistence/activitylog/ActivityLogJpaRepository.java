package gytis.courier.adapter.out.persistence.activitylog;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogJpaRepository extends JpaRepository<ActivityLogJpaEntity, Long> {

}
