package gytis.courier.adapter.out.persistence.banhistory;

import gytis.courier.adapter.out.persistence.banhistory.projection.BanHistoryProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BanHistoryJpaRepository extends JpaRepository<BanHistoryJpaEntity, Long> {
    List<BanHistoryProjection> findAllByPersonId(Long personId);
}
