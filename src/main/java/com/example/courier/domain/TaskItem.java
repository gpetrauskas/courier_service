package com.example.courier.domain;

import com.example.courier.common.ParcelStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "delivery_task_items")
public class TaskItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parcel_id", nullable = false)
    private Parcel parcel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParcelStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_address_id", nullable = false)
    private OrderAddress senderAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_address_id", nullable = false)
    private OrderAddress recipientAddress;

    @Column(name = "delivery_preference", nullable = false)
    private String deliveryPreference;

    @ElementCollection
    @CollectionTable(name = "delivery_task_item_notes", joinColumns = @JoinColumn(name = "task_item_id"))
    @Column(name = "notes")
    private List<String> notes = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Parcel getParcel() {
        return parcel;
    }

    public void setParcel(Parcel parcel) {
        this.parcel = parcel;
    }

    public ParcelStatus getStatus() {
        return status;
    }

    public void setStatus(ParcelStatus status) {
        this.status = status;
    }

    public OrderAddress getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(OrderAddress senderAddress) {
        this.senderAddress = senderAddress;
    }

    public OrderAddress getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(OrderAddress recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public String getDeliveryPreference() {
        return deliveryPreference;
    }

    public void setDeliveryPreference(String deliveryPreference) {
        this.deliveryPreference = deliveryPreference;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }

    public void addDefaultStatusChangeNote(Long personId, ParcelStatus newStatus) {
        String note = switch (newStatus) {
            case FAILED_PICKUP -> "Failed to pick up";
            case FAILED_DELIVERY -> "Failed to deliver";
            case PICKED_UP -> "Picked up";
            case DELIVERED -> "Delivered";
            default -> "Status changed";
        };
        notes.add(String.format("%s by Courier: %d for item id: %d", note, personId, getId()));
    }

    public void cancel() {
        if (ParcelStatus.cannotBeModified(this.status)) {
            throw new IllegalArgumentException("Parcel status cannot be changed");
        }

        setStatus(ParcelStatus.CANCELED);
        this.parcel.unassign();
    }

    public static TaskItem from(Parcel parcel, Order order, Task task) {
        TaskItem taskItem = new TaskItem();
        taskItem.parcel = parcel;
        taskItem.status = parcel.getStatus();
        taskItem.senderAddress = order.getSenderAddress();
        taskItem.recipientAddress = order.getRecipientAddress();
        taskItem.deliveryPreference = order.getDeliveryMethod();
        taskItem.task = task;
        parcel.assign();

        return taskItem;
    }

    public void addNote(String note) {
        if (note == null || note.isEmpty()) {
            throw new IllegalArgumentException("Note cannot be empty");
        }
        this.notes.add(note);
    }

    public void removeFromTask() {
        setStatus(ParcelStatus.REMOVED_FROM_THE_LIST);

        if (this.parcel != null) {
            parcel.unassign();
        }
    }

    public void changeStatus(@NotBlank ParcelStatus status, @NotNull Long personId) {
        if (this.getStatus().isFinalState()) {
            throw new IllegalArgumentException("Task item cannot be updated anymore.");
        }

        this.setStatus(status);
        this.addDefaultStatusChangeNote(personId, status);
    }

    public void applyFinalStatusToParcel() {
        if (this.parcel== null) {
            throw new IllegalStateException("TaskItem must be associated with a Parcel");
        }
        this.parcel.updateStatusFromTaskItem(this.status);
    }
}
