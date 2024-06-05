/*package com.example.courier.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "couriers")
public class Courier extends User {

    public Courier() {
        super();
    }

    @OneToMany(mappedBy = "courier", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Order> assignedOrders = new ArrayList<>();

    public List<Order> getAssignedOrders() {
        return assignedOrders;
    }

    public void setAssignedOrders(List<Order> assignedOrders) {
        this.assignedOrders = assignedOrders;
    }

    public void addOrder(Order order) {
        this.assignedOrders.add(order);
        order.setCourier(this);
    }

    public void removeOrder(Order order) {
        this.assignedOrders.remove(order);
        order.setCourier(null);
    }
}


 */