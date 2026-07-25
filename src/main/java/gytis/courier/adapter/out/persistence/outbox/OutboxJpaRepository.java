package gytis.courier.adapter.out.persistence.outbox;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxJpaRepository extends JpaRepository<OutboxJpaEntity, Long> {
    List<OutboxJpaEntity> findByStatusOrderByCreatedAt(OutboxEnum status, Pageable pageable);
}
