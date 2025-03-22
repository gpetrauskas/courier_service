package com.example.courier.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

    @Entity
    @Table(name = "couriers")
    public class Courier extends Person {

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

    public void setHasActiveTask(boolean hasActiveTask) {
        this.hasActiveTask = hasActiveTask;
    }
}