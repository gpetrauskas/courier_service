package gytis.courier.adapter.out.persistence.ticket;

import gytis.courier.adapter.out.persistence.person.common.PersonJpaEntity;
import gytis.courier.domain.ticket.TicketPriority;
import gytis.courier.domain.ticket.TicketStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tickets")
public class TicketJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    private TicketPriority priority;

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", insertable = false, updatable = false)
    private PersonJpaEntity createdBy;

    @Column(name = "created_by_id")
    private Long createdById;
    private Long assignedTo;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<TicketCommentJpaEntity> comments = new ArrayList<>();

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TicketStatus getStatus() { return status; }
    public TicketPriority getPriority() { return priority; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<TicketCommentJpaEntity> getComments() { return comments; }
    public PersonJpaEntity getCreatedBy() { return createdBy; }
    public Long getAssignedTo() { return assignedTo; }
    public Long getCreatedById() { return createdById; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(TicketStatus status) { this.status = status; }
    public void setPriority(TicketPriority priority) { this.priority = priority; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setComments(List<TicketCommentJpaEntity> comments) { this.comments = comments; }
    public void setCreatedBy(PersonJpaEntity createdBy) { this.createdBy = createdBy; }
    public void setAssignedTo(Long assignedTo) { this.assignedTo = assignedTo; }
    public void setCreatedById(Long createdById) { this.createdById = createdById; }
}
