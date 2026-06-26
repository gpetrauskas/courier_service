package gytis.courier.domain.task;

import gytis.courier.domain.order.ParcelStatus;

import java.util.*;

public class TaskItem {
    private Long id;
    private Long parcelId;
    private ParcelStatus parcelStatus;
    private Long senderAddressId;
    private Long recipientAddressId;
    private String deliveryMethodName;
    private String contents;
    private final Set<String> notes = new HashSet<>();

    protected TaskItem() {}

    public Long getId() { return id; }
    public Long getParcelId() { return parcelId; }
    public ParcelStatus getParcelStatus() { return parcelStatus; }
    public Long getSenderAddressId() { return senderAddressId; }
    public Long getRecipientAddressId() { return recipientAddressId; }
    public String getDeliveryMethodName() { return deliveryMethodName; }
    public String getContents() { return contents; }
    public Set<String> getNotes() { return notes; }

    public static Builder restore() {
        return new Builder();
    }

    public static TaskItem create(TaskItemCreationSnapshot snapshot) {
        Objects.requireNonNull(snapshot);
        if (snapshot.status().availableForTask()) {
            throw new IllegalStateException("Parcel with ID: " + snapshot.parcelId() + " and status: " + snapshot.status() + " is in final state and cannot be added");
        }

        TaskItem taskItem = new TaskItem();
        taskItem.parcelId = Objects.requireNonNull(snapshot.parcelId());
        taskItem.parcelStatus = Objects.requireNonNull(snapshot.status());
        taskItem.senderAddressId = Objects.requireNonNull(snapshot.senderAddressId());
        taskItem.recipientAddressId = Objects.requireNonNull(snapshot.recipientAddressId());
        taskItem.deliveryMethodName = Objects.requireNonNull(snapshot.deliveryMethodName());
        taskItem.contents = snapshot.contents();

        return taskItem;
    }

    public void cancel() {
        if (this.parcelStatus.preventsTaskCancel()) {
            throw new IllegalStateException("Item cannot be removed");
        }

        this.parcelStatus = ParcelStatus.CANCELED;
    }

    public void setSenderAddressId(Long id) {
        this.recipientAddressId = id;
    }

    public void setRecipientAddressId(Long id) {
        this.recipientAddressId = id;
    }

    public void updateStatus(ParcelStatus status) {
        if (!this.parcelStatus.isValidTransition(status)) {
            throw new IllegalStateException("Item status " + this.parcelStatus + " cannot be changed to : " + status);
        }

        this.parcelStatus = status;
    }

    public void addNote(String note) {
        Objects.requireNonNull(note);

        validateIfItemIsNotInFinalState();
        this.notes.add(note);
    }

    public void remove() {
        validateIfItemIsNotInFinalState();
        this.parcelStatus = ParcelStatus.REMOVED_FROM_THE_LIST;
    }

    private void validateIfItemIsNotInFinalState() {
        if (parcelStatus.isFinalState()) {
            throw new IllegalStateException("Item is in final state");
        }
    }

    public static class Builder {
        private Long id;
        private Long parcelId;
        private ParcelStatus parcelStatus;
        private Long senderAddressId;
        private Long recipientAddressId;
        private String deliveryMethodName;
        private String contents;
        private Set<String> notes;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        public Builder parcelId(Long parcelId) {
            this.parcelId = parcelId;
            return this;
        }
        public Builder parcelStatus(ParcelStatus parcelStatus) {
            this.parcelStatus = parcelStatus;
            return this;
        }
        public Builder senderAddressId(Long senderAddressId) {
            this.senderAddressId = senderAddressId;
            return this;
        }
        public Builder recipientAddressId(Long recipientAddressId) {
            this.recipientAddressId = recipientAddressId;
            return this;
        }
        public Builder deliveryMethodName(String deliveryMethodName) {
            this.deliveryMethodName = deliveryMethodName;
            return this;
        }
        public Builder contents(String contents) {
            this.contents= contents;
            return this;
        }
        public Builder notes(Set<String> notes) {
            this.notes = notes;
            return this;
        }

        public TaskItem build() {
            TaskItem item = new TaskItem();
            item.id = id;
            item.parcelId = parcelId;
            item.parcelStatus = parcelStatus;
            item.senderAddressId = senderAddressId;
            item.recipientAddressId = recipientAddressId;
            item.deliveryMethodName = deliveryMethodName;
            item.contents = contents;

            if (this.notes != null) {
                item.notes.addAll(notes);
            }

            return item;
        }
    }
}
