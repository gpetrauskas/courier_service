package gytis.courier.adapter.out.persistence.ticket;

import gytis.courier.adapter.out.persistence.ticket.projection.TicketProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TicketJpaRepository extends JpaRepository<TicketJpaEntity, Long> {
    @EntityGraph(attributePaths = "comments")
    Optional<TicketJpaEntity> findWithCommentsById(Long id);

    boolean existsByIdAndCreatedById(Long ticketId, Long authorId);

    @Query("SELECT t FROM TicketJpaEntity t WHERE t.createdById = :owner")
    Page<TicketProjection> findAllProjectedByCreatedById(@Param("owner") Long myId, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE TicketJpaEntity t SET t.updatedAt = :timestamp WHERE t.id = :id")
    void saveTimestamp(@Param("id") Long id, @Param("timestamp") LocalDateTime timestamp);

    @EntityGraph(attributePaths = "createdBy")
    Page<TicketProjection> findAllProjectedBy(Pageable pageable);

}
