package com.example.courier.domain;


import com.example.courier.common.OrderStatus;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "sender_address_id", nullable = false)
    private OrderAddress senderAddress;

    @ManyToOne
    @JoinColumn(name = "recipient_address_id", nullable = false)
    private OrderAddress recipientAddress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parcel_id", referencedColumnName = "id")
    private Parcel parcelDetails;

    @Column(nullable = false)
    private String deliveryMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private LocalDateTime createDate;

    public Order() {}

    public Order(User user, OrderAddress senderAddress, OrderAddress recipientAddress, Parcel parcelDetails, String deliveryMethod, OrderStatus status, LocalDateTime createDate) {
        this.user = user;
        this.senderAddress = senderAddress;
        this.recipientAddress = recipientAddress;
        this.parcelDetails = parcelDetails;
        this.deliveryMethod = deliveryMethod;
        this.status = status;
        this.createDate = createDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Parcel getParcelDetails() {
        return parcelDetails;
    }

    public void setParcelDetails(Parcel parcelDetails) {
        this.parcelDetails = parcelDetails;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
