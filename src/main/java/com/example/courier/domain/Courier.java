package com.example.courier.domain;

import jakarta.persistence.*;
import jakarta.validation.ValidationException;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "couriers")
public class Courier extends Person {

    public Courier() {}

    public Courier(String name, String email, String password) {
        super(name, email, password);
    }

    @OneToMany(mappedBy = "courier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> assignedTasks = new ArrayList<>();

    @Column(nullable = false)
    private boolean hasActiveTask = false;

    @Override
    public String getRole() {
        return "COURIER";
    }

    public List<Task> getTasks() {
        return assignedTasks;
    }

    public void setTasks(List<Task> tasks) {
        this.assignedTasks = tasks;
    }

    public boolean hasActiveTask() {
        return hasActiveTask;
    }

    public void activateTask() {
        if (this.hasActiveTask) {
            throw new ValidationException("Courier " + this.getName() + " already has an active task");
        }

        this.hasActiveTask = true;
    }

    public void completeTask() {
        if (!this.hasActiveTask) {
            throw new ValidationException("Courier " + this.getName() + " already without any active task");
        }
        this.hasActiveTask = false;
    }
}