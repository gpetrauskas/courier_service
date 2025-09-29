package com.example.courier.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "order_addresses")
public class OrderAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private AddressDetails details;

    protected OrderAddress() {}

    public OrderAddress(AddressDetails details) {
        this.details = details;
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
}
