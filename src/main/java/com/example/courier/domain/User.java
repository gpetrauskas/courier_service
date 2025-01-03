package com.example.courier.domain;

import com.example.courier.common.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class User extends Person implements Serializable {


    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "default_address_id")
    private Address defaultAddress;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<PaymentMethod> paymentMethods;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;

    @Column(name = "is_blocked", nullable = false)
    @ColumnDefault("false")
    private boolean isBlocked;

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("false")
    private boolean isDeleted;

    public User() {
        super();
        this.paymentMethods = new ArrayList<>();
    }

    public User(String name, String email, String password) {
        super(name, email, password, Role.USER);
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public List<PaymentMethod> test() {
        return this.paymentMethods.stream()
                .filter(paymentMethod -> paymentMethod.isCreditCard())
                .map(paymentMethod -> (CreditCard) paymentMethod)
                .filter(CreditCard::isSaved)
                .collect(Collectors.toList());
    }

    public List<PaymentMethod> getPaymentMethods() {
        List<PaymentMethod> savedPaymentMethods = new ArrayList<>();
        for (PaymentMethod paymentMethod : this.paymentMethods) {
            if (paymentMethod instanceof CreditCard) {
                CreditCard card = (CreditCard) paymentMethod;
                if (card.isSaved()) {
                    savedPaymentMethods.add(card);
                }
            }
        }

        return savedPaymentMethods;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public Address getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(Address defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public boolean isDeleted() {
        return this.isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
