package com.example.courier.domain;

import com.example.courier.common.TicketPriority;
import com.example.courier.common.TicketStatus;
import com.example.courier.dto.request.ticket.TicketCreateRequestDTO;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    private TicketPriority priority;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<TicketComment> comments = new HashSet<>();

    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    private Person createdBy;

    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private Person assignedTo;

    protected Ticket() {
        this.status = TicketStatus.OPEN;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Person getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Person createdBy) {
        Objects.requireNonNull(createdBy, "user cannot be nul");

        this.createdBy = createdBy;
    }

    public Person getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Person assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Set<TicketComment> getComments() {
        return comments;
    }

    private void attachComment(TicketComment comment) {
        assertCommentAllowed();

        this.comments.add(comment);
        comment.setTicket(this);
    }

    public void removeComment(TicketComment comment) {
        this.comments.remove(comment);
        comment.setTicket(null);
    }

    public void assertCommentAllowed() {
        if (this.getStatus().isClosed()) {
            throw new IllegalStateException("Cannot comment on a closed ticket");
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public TicketComment addComment(Person author, String message) {
        TicketComment ticketComment = new TicketComment();
        ticketComment.setAuthor(author);
        ticketComment.setMessage(message);
        ticketComment.setCreatedAt(LocalDateTime.now());

        this.updatedAt = LocalDateTime.now();

        attachComment(ticketComment);
        return ticketComment;
    }

    public static Ticket create(TicketCreateRequestDTO requestDTO, Person creator) {
        Objects.requireNonNull(requestDTO, "Ticket creation dto cannot be null");
        Objects.requireNonNull(creator);

        Ticket ticket = new Ticket();
        ticket.title = Objects.requireNonNull(requestDTO.title());
        ticket.description = Objects.requireNonNull(requestDTO.description());
        ticket.changePriority(requestDTO.priority());
        ticket.createdBy = creator;

        return ticket;
    }

    public void changeStatus(TicketStatus newStatus) {
        if (!TicketStatus.isValidTransition(this.status, newStatus)) {
            throw new IllegalStateException("Transition to " + newStatus + " is not valid");
        }

        this.status = newStatus;
    }

    public void changePriority(TicketPriority priority) {
        if (!TicketPriority.isValidPriority(priority.name())) {
            throw new IllegalStateException("Given priority status is not valid");
        }

        this.priority = priority;
    }
}
