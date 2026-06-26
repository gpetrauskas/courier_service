package gytis.courier.adapter.out.persistence.ticket;

import gytis.courier.adapter.out.persistence.ticket.projection.TicketCommentProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TicketCommentJpaRepository extends JpaRepository<TicketCommentJpaEntity, Long> {

    @Query("""
        SELECT
            c.message AS message,
            c.createdAt AS createdAt,
            c.author.name AS authorName
        FROM TicketCommentJpaEntity c
        JOIN c.author a
        WHERE c.ticket.id = :ticketId
        ORDER BY c.createdAt ASC
""")
    Page<TicketCommentProjection> findByTicketId(Long ticketId, Pageable pageable);
}
