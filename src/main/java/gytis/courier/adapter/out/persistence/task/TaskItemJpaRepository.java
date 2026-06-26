package gytis.courier.adapter.out.persistence.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskItemJpaRepository extends JpaRepository<TaskItemJpaEntity, Long> {
    Optional<TaskItemJpaEntity> findByTaskIdAndParcelId(Long taskId, Long parcelId);
}
