package com.example.courier.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class User extends Person {

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

    @Column
    @ColumnDefault("false")
    private Boolean subscribed;

    public User() {
        super();
        this.paymentMethods = new ArrayList<>();
    }

    @Override
    public String getRole() {
        return "USER";
    }

    public User(String name, String email, String password) {
        super(name, email, password);
    }

    public List<PaymentMethod> test() {
        return this.paymentMethods.stream()
                .filter(PaymentMethod::isCreditCard)
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

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(Boolean subscribed) {
        if (!Objects.equals(this.subscribed, subscribed)) {
            this.subscribed = subscribed;
        }
    }

    public Optional<Address> getAddressById(Long id) {
        return addresses.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst();
    }
}
