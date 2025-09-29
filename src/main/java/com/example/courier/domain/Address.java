package com.example.courier.domain;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Embedded
    private AddressDetails details;

    protected Address() {}

    public Address(User user, AddressDetails details) {
        this.user = user;
        this.details = details;
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AddressDetails getDetails() {
        return details;
    }

    public void updateAddress(AddressDetails details) {
        this.details = details;
    }
}
