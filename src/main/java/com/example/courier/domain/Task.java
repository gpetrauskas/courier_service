
package com.example.courier.domain;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.common.TaskType;
import com.example.courier.dto.CreateTaskDTO;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "delivery_tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id", nullable = false)
    private Courier courier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin createdBy;

    @Column(name = "canceled_by_admin_id")
    private Long canceledByAdminId;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaskItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType taskType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus deliveryStatus;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = true)
    private LocalDateTime completedAt;

    public Long getId() {
        return id;
    }

    public Courier getCourier() {
        return courier;
    }

    public void setCourier(Courier courier) {
        this.courier = courier;
    }

    public Admin getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Admin createdBy) {
        this.createdBy = createdBy;
    }

    public List<TaskItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void setItems(List<TaskItem> items) {
        this.items = items;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Long getCanceledByAdminId() {
        return canceledByAdminId;
    }

    public void setCanceledByAdminId(Long canceledByAdminId) {
        this.canceledByAdminId = canceledByAdminId;
    }

    public void completeOnCheckIn() {
        if (this.deliveryStatus != DeliveryStatus.RETURNING_TO_STATION) {
            throw new IllegalArgumentException("Invalid status for check in.");
        }
        this.deliveryStatus = DeliveryStatus.AT_CHECKPOINT;
        this.completedAt = LocalDateTime.now();
        this.courier.setHasActiveTask(false);
    }

    public void markAsComplete() {
        this.deliveryStatus = DeliveryStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
       // updateItemsOnCompletion();
    }

/*    private void updateItemsOnCompletion() {
        this.items.forEach(item -> {
            if (item.getStatus() == ParcelStatus.PICKED_UP) {
                item.setStatus(ParcelStatus.DELIVERING);
            } else if (item.getTask())
        });
    }*/

    public void initiateTaskCreation(CreateTaskDTO taskDTO, Courier courier, Admin admin) {
        setCourier(courier);
        setCreatedBy(admin);
        setTaskType(taskDTO.taskType().equals("PICKING_UP") ? TaskType.PICKUP : TaskType.DELIVERY);
        setDeliveryStatus(DeliveryStatus.IN_PROGRESS);
    }

    public void updateStatusIfAllItemsFinal() {
        if (this.items.stream().allMatch(item -> item.getStatus().isFinalState())) {
            setDeliveryStatus(DeliveryStatus.RETURNING_TO_STATION);
        }
    }

    public void cancelItems() {
        this.items.forEach(TaskItem::cancel);
    }

    public boolean areAllItemsCanceled() {
        return this.items.stream()
                .allMatch(item -> item.getStatus() == ParcelStatus.CANCELED ||
                        item.getStatus() == ParcelStatus.REMOVED_FROM_THE_LIST);
    }

    public void cancel(Long adminId) {
        this.courier.setHasActiveTask(false);
        this.setDeliveryStatus(DeliveryStatus.CANCELED);
        this.setCanceledByAdminId(adminId);
    }

    public void addTaskItems(List<TaskItem> taskItems) {
        if (this.deliveryStatus.isFinalState()) {
            throw new IllegalStateException("Cannot add items to a final state task.");
        }
        taskItems.forEach(item -> item.setTask(this));
        this.items.addAll(taskItems);
    }

    public void addTaskItem(TaskItem item) {
        if (this.deliveryStatus.isFinalState()) {
            throw new IllegalStateException("Cannot add items to a final state task.");
        }

        item.setTask(this);
        this.items.add(item);
    }
}
