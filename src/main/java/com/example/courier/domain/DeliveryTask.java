/*
package com.example.courier.domain;

import com.example.courier.common.DeliveryStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "delivery_list")
public class DeliveryList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "courier_id", nullable = false)
    private User courier;

    @OneToMany
    @JoinTable(
            name = "delivery_list_packages",
            joinColumns = @JoinColumn(name = "delivery_list_id"),
            inverseJoinColumns = @JoinColumn(name = "package_id")
    )
    private List<Package> packages;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public DeliveryList() {

    }
}


 */