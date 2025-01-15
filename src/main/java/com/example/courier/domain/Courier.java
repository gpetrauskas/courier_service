package com.example.courier.domain;

import jakarta.persistence.*;

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
}