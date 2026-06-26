package gytis.courier.adapter.out.persistence.ticket;

import gytis.courier.adapter.out.persistence.person.common.PersonJpaEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_comments")
public class TicketCommentJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "author_id")
    private Long authorId;
    @Column(name = "ticket_id")
    private Long ticketId;
    private String message;
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", updatable = false, insertable = false)
    private TicketJpaEntity ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", insertable = false, updatable = false)
    private PersonJpaEntity author;

    public Long getId() { return id; }
    public Long getAuthorId() { return authorId; }
    public String getMessage() { return message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Long getTicketId() { return this.ticketId; }

    public void setId(Long id) { this.id = id; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public void setMessage(String message) { this.message = message; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
    public void setTicket(TicketJpaEntity ticket) { this.ticket = ticket; }
}
