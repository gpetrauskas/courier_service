package com.example.courier.domain;

import com.example.courier.common.PackageStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "delivery_task_items")
public class DeliveryTaskItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private DeliveryTask task;

    @ManyToOne
    @JoinColumn(name = "package_id", nullable = false)
    private Package parcel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PackageStatus status;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DeliveryTask getTask() {
        return task;
    }

    public void setTask(DeliveryTask task) {
        this.task = task;
    }

    public Package getParcel() {
        return parcel;
    }

    public void setParcel(Package parcel) {
        this.parcel = parcel;
    }

    public PackageStatus getStatus() {
        return status;
    }

    public void setStatus(PackageStatus status) {
        this.status = status;
    }

}
