package gytis.courier.domain.ticket;

import gytis.courier.application.command.CreateTicketCommand;
import gytis.courier.exception.ResourceNotFoundException;
import gytis.courier.exception.TicketClosedException;

import java.time.LocalDateTime;
import java.util.*;

public class Ticket {
    private Long id;
    private String title;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private LocalDateTime createdAt;
    private final List<TicketComment> comments = new ArrayList<>();
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long assignedTo;

    protected Ticket() {}

    public static Ticket create(CreateTicketCommand command) {
        Ticket ticket = new Ticket();
        ticket.title = Objects.requireNonNull(command.title());
        ticket.description = Objects.requireNonNull(command.description());
        ticket.priority = Objects.requireNonNull(command.ticketPriority());
        ticket.createdBy = Objects.requireNonNull(command.personId());
        ticket.status = TicketStatus.OPEN;
        ticket.createdAt = LocalDateTime.now();

        return ticket;
    }

    public static Ticket restore(Long id, String title, String description, TicketStatus status, TicketPriority priority, LocalDateTime createdAt,
                                 LocalDateTime updatedAt, Long createdBy, Long assignedTo, List<TicketComment> comments) {
        Ticket ticket = new Ticket();
        ticket.id = id;
        ticket.title = title;
        ticket.description = description;
        ticket.status = status;
        ticket.priority = priority;
        ticket.createdAt = createdAt;
        ticket.updatedAt = updatedAt;
        ticket.createdBy = createdBy;
        ticket.assignedTo = assignedTo;

        if (comments != null) {
            ticket.comments.addAll(comments);
        }

        return ticket;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TicketStatus getStatus() { return status; }
    public TicketPriority getPriority() { return priority; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<TicketComment> getComments() { return comments; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Long getCreatedBy() { return createdBy; }
    public Long getAssignedTo() { return assignedTo; }

    private void assertNotClosed() {
        if (this.status.isClosed()) {
            throw new TicketClosedException("Ticket is closed and new comments cannot be added");
        }
    }

    public void changeStatus(TicketStatus newStatus) {
        Objects.requireNonNull(newStatus);
        assertNotClosed();

        if (newStatus != this.status) {
            this.status = newStatus;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void changePriority(TicketPriority priority) {
        Objects.requireNonNull(priority);
        assertNotClosed();

        if (this.priority != priority) {
            this.priority = priority;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public TicketComment addComment(Long userId, boolean isAdmin, String message) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(message);
        assertNotClosed();

        validateIfCanAdd(userId, isAdmin);

        TicketComment comment = TicketComment.create(userId, message);
        comments.add(comment);
        updatedAt = LocalDateTime.now();

        return comment;
    }

    public void close(Long userId) {
        Objects.requireNonNull(userId);

        if (!this.createdBy.equals(userId)) {
            throw new ResourceNotFoundException("No ticket found for current user");
        }

        this.changeStatus(TicketStatus.CLOSED);
    }

    private void validateIfCanAdd(Long userId, boolean isAdmin) {
        if (!isAdmin && !this.createdBy.equals(userId)) {
            throw new IllegalStateException("No permission to the ticket");
        }
    }
}
