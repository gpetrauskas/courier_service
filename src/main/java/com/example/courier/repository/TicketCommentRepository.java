package com.example.courier.repository;

import com.example.courier.domain.TicketComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {
    Page<TicketComment> findByTicketIdOrderByCreatedAtAsc(Long ticketId, Pageable pageable);
}
