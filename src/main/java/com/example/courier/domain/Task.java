
package com.example.courier.domain;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.common.TaskType;
import com.example.courier.exception.TaskNotCancelableException;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private final List<TaskItem> items = new ArrayList<>();

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

    protected Task() {}

    public Long getId() {
        return id;
    }

    public Courier getCourier() {
        return courier;
    }

    private void setCourier(Courier courier) {
        this.courier = courier;
    }

    public Admin getCreatedBy() {
        return createdBy;
    }

    private void setCreatedBy(Admin createdBy) {
        this.createdBy = createdBy;
    }

    public List<TaskItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public TaskType getTaskType() {
        return taskType;
    }

    private void setTaskType(TaskType taskType) {
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
        if (!this.deliveryStatus.canTransitionTo(DeliveryStatus.AT_CHECKPOINT)) {
            throw new IllegalArgumentException("Invalid status for check in.");
        }
        this.deliveryStatus = DeliveryStatus.AT_CHECKPOINT;
        this.completedAt = LocalDateTime.now();
        this.courier.completeTask();
    }

    public void changeStatusAsAdmin(DeliveryStatus newStatus, Long adminId) {
        Objects.requireNonNull(newStatus, "Status must be provided");
        if (!this.deliveryStatus.canTransitionTo(newStatus)) {
            throw new IllegalArgumentException("Task status cannot be changed.");
        }

        switch (newStatus) {
            case COMPLETED -> completeTask();
            case CANCELED -> cancelWithItems(adminId);
            case RETURNING_TO_STATION -> this.setDeliveryStatus(DeliveryStatus.RETURNING_TO_STATION);
            default -> throw new IllegalArgumentException("Unsupported status change.");
        }
    }

    public void updateStatusIfAllItemsFinal() {
        if (this.items.stream().allMatch(item -> item.getStatus().isFinalState())) {
            setDeliveryStatus(DeliveryStatus.RETURNING_TO_STATION);
        }
    }

    public boolean isAllItemsCanceled() {
        return this.items.stream()
                .allMatch(item -> item.getStatus() == ParcelStatus.CANCELED ||
                        item.getStatus() == ParcelStatus.REMOVED_FROM_THE_LIST);
    }

    public void cancel(Long adminId) {
        Objects.requireNonNull(adminId, "Admin ID must be provided.");
        validateCancelable();

        this.courier.completeTask();
        this.setDeliveryStatus(DeliveryStatus.CANCELED);
        this.setCanceledByAdminId(adminId);
    }

    public void cancelWithItems(Long adminId) {
        Objects.requireNonNull(adminId, "adminId null");
        items.stream()
                .filter(i -> !i.getStatus().isAlreadyCanceledOrRemoved())
                .forEach(TaskItem::cancel);

        if (!isAllItemsCanceled()) {
            String notCanceled = items.stream()
                    .filter(i -> i.getStatus() != ParcelStatus.CANCELED && i.getStatus() != ParcelStatus.REMOVED_FROM_THE_LIST)
                    .map(i -> i.getId().toString())
                    .collect(Collectors.joining(", "));
            throw new TaskNotCancelableException("Some items cannot be canceled: " + notCanceled);
        }

        cancel(adminId);
    }

    public void addTaskItems(List<TaskItem> taskItems) {
        Objects.requireNonNull(taskItems, "Task items list cannot be null");

        if (this.deliveryStatus.isFinalState()) {
            throw new IllegalStateException("Cannot add items to a final state task.");
        }
        taskItems.forEach(item -> item.setTask(this));
        this.items.addAll(taskItems);
    }

    public void addTaskItem(TaskItem item) {
        Objects.requireNonNull(item, "Item cannot be null");

        if (this.deliveryStatus.isFinalState()) {
            throw new IllegalStateException("Cannot add items to a final state task.");
        }

        item.setTask(this);
        this.items.add(item);
    }

    public void completeTask() {
        if (this.getItems().stream().anyMatch(item -> !item.getStatus().isFinalState())) {
            throw new IllegalArgumentException("All items must be in final state;");
        }

        this.getItems().forEach(TaskItem::applyFinalStatusToParcel);
        this.setDeliveryStatus(DeliveryStatus.COMPLETED);
    }

    public void validateCancelable() {
        if (this.deliveryStatus.isFinalState()) {
            throw new TaskNotCancelableException("Task is in final state or already canceled");
        }

        if (this.items.stream().anyMatch(i -> i.getStatus().preventsTaskCancel())) {
            throw new TaskNotCancelableException("Task cannot be canceled. One or more item state blocking it.");
        }
    }

    public static Task create(String taskType, Courier courier, Admin admin) {
        Objects.requireNonNull(taskType);
        Objects.requireNonNull(courier);
        Objects.requireNonNull(admin);


        Task task = new Task();
        task.setCourier(courier);
        task.setCreatedBy(admin);
        task.setTaskType(TaskType.fromString(taskType));
        task.setDeliveryStatus(DeliveryStatus.IN_PROGRESS);

        return task;
    }
}
