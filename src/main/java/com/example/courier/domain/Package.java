package com.example.courier.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "packages")
public class Package {

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

    @Column(nullable = false)
    private String status;

    public Package() {}

    public Package(String weight, String dimensions, String contents, String trackingNumber, String status) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
