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

    @ManyToOne
    @JoinColumn(name = "weight_id", nullable = false)
    private DeliveryMethod weight;

    @ManyToOne
    @JoinColumn(name = "dimensions_id", nullable = false)
    private DeliveryMethod dimensions;

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

    public Parcel(DeliveryMethod weight, DeliveryMethod dimensions, String contents, String trackingNumber, ParcelStatus status) {
        this.weight = weight;
        this.dimensions = dimensions;
        this.contents = contents;
        this.trackingNumber = trackingNumber;
        this.status = status;
    }

    public void cancel() {
        this.status = ParcelStatus.CANCELED;
    }

    public DeliveryMethod getWeight() {
        return weight;
    }

    public void setWeight(DeliveryMethod weight) {
        this.weight = weight;
    }

    public DeliveryMethod getDimensions() {
        return dimensions;
    }

    public void setDimensions(DeliveryMethod dimensions) {
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

    public void unassign() {
        this.isAssigned = false;
    }

    public void assign() {
        if (isAssigned) {
            throw new IllegalStateException("Parcel is already assigned.");
        }
        this.isAssigned = true;
    }

    public void updateStatusFromTaskItem(ParcelStatus status) {
        setStatus(status);
        unassign();
    }

    public void transitionToDelivery() {
        if (this.status != ParcelStatus.PICKED_UP) {
            throw new IllegalStateException(
                    String.format("Cannot transition to DELIVERY from %s", this.status)
            );
        }

        this.status = ParcelStatus.DELIVERING;
    }

}
