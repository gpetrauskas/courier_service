package com.example.courier.repository;

import com.example.courier.domain.Ticket;
import com.example.courier.repository.projection.TicketAccessIdsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {
    Page<Ticket> findAllByCreatedById(Long personId, Pageable pageable);
    //<Ticket> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"createdBy", "assignedTo"})
    Optional<Ticket> findWithRelationsById(Long id);

    Optional<TicketAccessIdsProjection> findAccessIdsById(Long ticketId);
}
