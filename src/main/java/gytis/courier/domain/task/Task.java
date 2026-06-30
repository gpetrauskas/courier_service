package gytis.courier.domain.task;

import gytis.courier.domain.event.*;
import gytis.courier.domain.order.ParcelStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Task {
    private Long id;
    private Long courierId;
    private Long createdByAdminId;
    private Long canceledByAdminId;
    private final List<TaskItem> taskItems = new ArrayList<>();
    private TaskType taskType;
    private DeliveryStatus deliveryStatus;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private final List<DomainEvent> events = new ArrayList<>();

    protected Task() {}

    public Long getId() { return id; }
    public Long getCourierId() { return courierId; }
    public Long getCreatedByAdminId() { return createdByAdminId; }
    public Long getCanceledByAdminId() { return canceledByAdminId; }
    public List<TaskItem> getTaskItems() { return Collections.unmodifiableList(this.taskItems); }
    public TaskType getTaskType() { return taskType; }
    public DeliveryStatus getDeliveryStatus() { return deliveryStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }

    private TaskItem findByItemId(Long id) {
        return taskItems.stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElseThrow();
    }

    public List<DomainEvent> pullEvents() {
        List<DomainEvent> currentEvents = new ArrayList<>(events);
        events.clear();
        return currentEvents;
    }

    public static Task create(List<TaskItemCreationSnapshot> snapshots, Long courierId, Long adminId, TaskType taskType) {
        Objects.requireNonNull(snapshots);
        Objects.requireNonNull(adminId);
        Objects.requireNonNull(courierId);
        Objects.requireNonNull(taskType);

        Task task = new Task();
        task.courierId = courierId;
        task.createdByAdminId = adminId;
        task.taskType = taskType;
        task.deliveryStatus = DeliveryStatus.IN_PROGRESS;
        task.createdAt = LocalDateTime.now();
        task.events.add(new TaskAssignedEvent(courierId));

        for (TaskItemCreationSnapshot snapshot : snapshots) {
            TaskItem taskItem = TaskItem.create(snapshot);
            task.taskItems.add(taskItem);
        }

        return task;
    }

    public static Builder restore() {
        return new Builder();
    }

    public void addItems(List<TaskItemCreationSnapshot> snapshots) {
        validateIfTaskIsNotInFinalState();

        snapshots.forEach(i -> {
            if (taskItems.stream().anyMatch(p -> p.getId().equals(i.parcelId()))) {
                throw new IllegalStateException("Item with ID " + i.parcelId() + " already in the list");
            }
            taskItems.add(TaskItem.create(i));
        });
    }

    public Optional<CourierChangeEvent> changeCourier(Long newCourier) {
        Objects.requireNonNull(newCourier);

        if (this.courierId.equals(newCourier)) return Optional.empty();

        Long oldCourier = this.courierId;
        this.courierId = newCourier;

        return Optional.of(new CourierChangeEvent(this.id, oldCourier, newCourier));
    }

    public List<Long> cancel(Long adminId) {
        canceledByAdminId = Objects.requireNonNull(adminId);

        validateIfCanTransitTo(DeliveryStatus.CANCELED);

        this.taskItems.stream()
                .filter(i -> !i.getParcelStatus().isAlreadyCanceledOrRemoved())
                .forEach(TaskItem::cancel);

        List<Long> parcelIds = taskItems.stream()
                .filter(i -> i.getParcelStatus() != ParcelStatus.REMOVED_FROM_THE_LIST)
                .map(TaskItem::getParcelId)
                .toList();

        deliveryStatus = DeliveryStatus.CANCELED;
        events.add(new TaskCanceledEvent(this.id, adminId));

        return parcelIds;
    }

    public void updateItemStatus(Long taskItemId, ParcelStatus status) {
        validateIfTaskIsNotInFinalState();

        TaskItem item = findByItemId(taskItemId);
        item.updateStatus(status);

        updateStatusIfAllItemsFinal();
    }

    public void addTaskItemNote(Long itemId, String note) {
        validateIfTaskIsNotInFinalState();

        TaskItem item = findByItemId(itemId);
        item.addNote(note);
    }

    public void complete() {
        validateIfCanTransitTo(DeliveryStatus.COMPLETED);
        validateIfAllItemsInFinalState();

        Map<Boolean, List<TaskItem>> sortedItems = this.taskItems.stream()
                .filter(s -> !s.getParcelStatus().isAlreadyCanceledOrRemoved())
                .collect(Collectors.groupingBy(
                s -> s.getParcelStatus().isFailed()
        ));

        this.deliveryStatus = DeliveryStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();

        events.add(
                new TaskCompletedEvent(
                        this.id,
                        this.courierId,
                        LocalDateTime.now(),
                        sortedItems.getOrDefault(true, List.of()).stream().map(TaskItem::getParcelId).toList(),
                        sortedItems.getOrDefault(false, List.of()).stream().map(i -> new ParcelStatusUpdate(i.getParcelId(), i.getParcelStatus())).toList())
                );
    }

    public Long removeItem(Long itemId, Long adminId) {
        if (this.deliveryStatus != DeliveryStatus.IN_PROGRESS) {
            throw new IllegalStateException("Item cannot be removed");
        }

        TaskItem item = findByItemId(itemId);
        item.remove();

        cancelIfNoItemsRemain(adminId);
        return item.getParcelId();
    }

    public void checkIn() {
        validateIfCanTransitTo(DeliveryStatus.AT_CHECKPOINT);
        validateIfAllItemsInFinalState();

        this.deliveryStatus = DeliveryStatus.AT_CHECKPOINT;
        this.completedAt = LocalDateTime.now();

        events.add(new CourierCheckedInEvent(id, courierId));
    }

    public void validateCourierOwnership(Long myId) {
        if (!this.courierId.equals(myId)) {
            throw new IllegalStateException("Task does not belong to current courier");
        }
    }

    private void cancelIfNoItemsRemain(Long adminId) {
        Objects.requireNonNull(adminId);

        if (this.taskItems.stream().allMatch(i -> i.getParcelStatus().isAlreadyCanceledOrRemoved())) {
            this.canceledByAdminId = adminId;
            this.deliveryStatus = DeliveryStatus.CANCELED;

            events.add(new TaskCanceledEvent(this.id, adminId));
        }
    }

    private void validateIfAllItemsInFinalState() {
        if (!taskItems.stream().allMatch(i -> i.getParcelStatus().isFinalState())) {
            throw new IllegalStateException("One or more item is not in the final state");
        }
    }

    private void validateIfCanTransitTo(DeliveryStatus newStatus) {
        if (!this.deliveryStatus.canTransitionTo(newStatus)) {
            throw new IllegalStateException("Current Task status: " + this.deliveryStatus + " cannot be transited to: " + newStatus);
        }
    }

    private void validateIfTaskIsNotInFinalState() {
        if (this.deliveryStatus.isFinalState()) {
            throw new IllegalStateException("Task is either completed or canceled");
        }
    }

    private void updateStatusIfAllItemsFinal() {
        if (this.taskItems.stream().allMatch(taskItem -> taskItem.getParcelStatus().isFinalState())) {
            this.deliveryStatus = DeliveryStatus.RETURNING_TO_STATION;

            events.add(new CourierReturningEvent(id, courierId));
        }
    }

    public static class Builder {
        private Long id;
        private Long courierId;
        private Long createdByAdminId;
        private Long canceledByAdminId;
        private TaskType taskType;
        private DeliveryStatus deliveryStatus;
        private LocalDateTime createdAt;
        private LocalDateTime completedAt;
        private List<TaskItem> taskItems;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        public Builder courierId(Long courierId) {
            this.courierId = courierId;
            return this;
        }
        public Builder createdByAdminId(Long createdByAdminId) {
            this.createdByAdminId = createdByAdminId;
            return  this;
        }
        public Builder canceledByAdminId(Long canceledByAdminId) {
            this.canceledByAdminId = canceledByAdminId;
            return this;
        }
        public Builder taskType(TaskType taskType) {
            this.taskType = taskType;
            return this;
        }
        public Builder deliveryStatus(DeliveryStatus deliveryStatus) {
            this.deliveryStatus = deliveryStatus;
            return this;
        }
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        public Builder completedAt(LocalDateTime completedAt) {
            this.completedAt = completedAt;
            return this;
        }
        public Builder items(List<TaskItem> taskItems) {
            this.taskItems = taskItems;
            return this;
        }

        public Task build() {
            Task task = new Task();
            task.id = this.id;
            task.courierId = this.courierId;
            task.createdByAdminId = this.createdByAdminId;
            task.canceledByAdminId = this.canceledByAdminId;
            task.taskType = this.taskType;
            task.deliveryStatus = this.deliveryStatus;
            task.createdAt = this.createdAt;
            task.completedAt = this.completedAt;

            if (this.taskItems != null) {
                task.taskItems.addAll(this.taskItems);
            }

            return task;
        }
    }


}

