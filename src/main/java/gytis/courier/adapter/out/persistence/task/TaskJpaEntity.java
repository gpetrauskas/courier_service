package gytis.courier.adapter.out.persistence.task;

import gytis.courier.adapter.out.persistence.person.courier.CourierJpaEntity;
import gytis.courier.domain.task.DeliveryStatus;
import gytis.courier.domain.task.TaskType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "delivery_tasks")
public class TaskJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "courier_id")
    private Long courierId;

    @Column(name = "admin_id", nullable = false)
    private Long createdByAdminId;

    @Column(name = "canceled_by_admin_id")
    private Long canceledByAdminId;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskItemJpaEntity> items;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType taskType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus deliveryStatus;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id", insertable = false, updatable = false)
    private CourierJpaEntity courier;

    public Long getId() { return id; }
    public Long getCourierId() { return courierId; }
    public Long getCreatedByAdminId() { return createdByAdminId; }
    public Long getCanceledByAdminId() { return canceledByAdminId; }
    public TaskType getTaskType() { return taskType; }
    public List<TaskItemJpaEntity> getItems() { return items; }
    public DeliveryStatus getDeliveryStatus() { return deliveryStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }

    public void setCourierId(Long courierId) { this.courierId = courierId; }
    public void setCreatedByAdminId(Long createdByAdminId) { this.createdByAdminId = createdByAdminId; }
    public void setCanceledByAdminId(Long canceledByAdminId) { this.canceledByAdminId = canceledByAdminId; }
    public void setTaskType(TaskType taskType) { this.taskType = taskType; }
    public void setItems(List<TaskItemJpaEntity> items) { this.items = items; }
    public void setDeliveryStatus(DeliveryStatus deliveryStatus) { this.deliveryStatus = deliveryStatus; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public CourierJpaEntity getCourier() { return courier; }
}
