package com.example.courier.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "couriers")
public class Courier extends Person {

    @OneToMany(mappedBy = "courier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DeliveryTask> assignedTasks;

    @Column(nullable = false)
    private boolean hasActiveTask = false;

    public List<DeliveryTask> getTasks() {
        return assignedTasks;
    }


    public void setTasks(List<DeliveryTask> tasks) {
        this.assignedTasks = tasks;
    }

    public boolean isHasActiveTask() {
        return hasActiveTask;
    }

    public void setHasActiveTask(boolean hasActiveTask) {
        this.hasActiveTask = hasActiveTask;
    }


/*
    @OneToMany(mappedBy = "courier", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Order> assignedOrders = new ArrayList<>();

    public List<Order> getAssignedOrders() {
        return assignedOrders;
    }

    public void setAssignedOrders(List<Order> assignedOrders) {
        this.assignedOrders = assignedOrders;
    }

 */
}