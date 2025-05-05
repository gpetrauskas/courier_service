package com.example.courier.repository;

import com.example.courier.domain.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {
    Page<Ticket> findAllByCreatedById(Long personId, Pageable pageable);
    Page<Ticket> findAll(Pageable pageable);
}
