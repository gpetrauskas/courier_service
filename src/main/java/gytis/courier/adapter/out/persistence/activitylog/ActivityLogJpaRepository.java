package gytis.courier.adapter.out.persistence.activitylog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActivityLogJpaRepository extends JpaRepository<ActivityLogJpaEntity, Long>, JpaSpecificationExecutor<ActivityLogJpaEntity> {


}
