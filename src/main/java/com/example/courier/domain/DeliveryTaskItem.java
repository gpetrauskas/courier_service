package com.example.courier.domain;

import com.example.courier.common.ParcelStatus;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "delivery_task_items")
public class DeliveryTaskItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private DeliveryTask task;

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

    public DeliveryTask getTask() {
        return task;
    }

    public void setTask(DeliveryTask task) {
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
}
