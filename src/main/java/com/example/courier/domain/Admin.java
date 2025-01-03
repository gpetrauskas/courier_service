package com.example.courier.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "admins")
public class Admin extends Person {

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DeliveryTask> createdTasks = new ArrayList<>();

    public List<DeliveryTask> getCreatedTasks() {
        return createdTasks;
    }

    public void setCreatedTasks(List<DeliveryTask> createdTasks) {
        this.createdTasks = createdTasks;
    }
}
