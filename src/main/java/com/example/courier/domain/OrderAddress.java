package com.example.courier.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "order_addresses")
public class OrderAddress extends BaseAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public OrderAddress() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
