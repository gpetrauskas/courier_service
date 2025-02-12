package com.example.courier.domain;

import com.example.courier.common.ParcelStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "parcels")
public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String weight;

    @Column(nullable = false)
    private String dimensions;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParcelStatus status;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean isAssigned;

    public Parcel() {}

    public Parcel(String weight, String dimensions, String contents, String trackingNumber, ParcelStatus status) {
        this.weight = weight;
        this.dimensions = dimensions;
        this.contents = contents;
        this.trackingNumber = trackingNumber;
        this.status = status;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public ParcelStatus getStatus() {
        return status;
    }

    public void setStatus(ParcelStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isAssigned() {
        return isAssigned;
    }

    public void setAssigned(boolean assigned) {
        isAssigned = assigned;
    }
}
