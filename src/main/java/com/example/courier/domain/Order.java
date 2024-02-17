package com.example.courier.domain;


import jakarta.persistence.*;

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

    @Column(nullable = false)
    private String senderAddress;

    @Column(nullable = false)
    private String recipientAddress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "package_id", referencedColumnName = "id")
    private Package packageDetails;

    @Column(nullable = false)
    private String deliveryPreferences;

    @Column(nullable = false)
    private String status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private LocalDateTime createDate;

    public Order() {}

    public Order(User user, String senderAddress, String recipientAddress, Package packageDetails, String deliveryPreferences, String status, LocalDateTime createDate) {
        this.user = user;
        this.senderAddress = senderAddress;
        this.recipientAddress = recipientAddress;
        this.packageDetails = packageDetails;
        this.deliveryPreferences = deliveryPreferences;
        this.status = status;
        this.createDate = createDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public Package getPackageDetails() {
        return packageDetails;
    }

    public void setPackageDetails(Package packageDetails) {
        this.packageDetails = packageDetails;
    }

    public String getDeliveryPreferences() {
        return deliveryPreferences;
    }

    public void setDeliveryPreferences(String deliveryPreferences) {
        this.deliveryPreferences = deliveryPreferences;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }
}
