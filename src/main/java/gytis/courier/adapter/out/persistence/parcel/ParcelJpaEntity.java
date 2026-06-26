package gytis.courier.adapter.out.persistence.parcel;

import gytis.courier.domain.order.ParcelStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "parcels")
public class ParcelJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "weight_id")
    private Long weightId;

    @Column(name = "weight_name")
    private String weightName;

    @Column(name = "weight_price")
    private BigDecimal weightPrice;

    @Column(name = "dimensions_id")
    private Long dimensionsId;

    @Column(name = "dimensions_name")
    private String dimensionsName;

    @Column(name = "dimensions_price")
    private BigDecimal dimensionsPrice;

    @Column
    private int failuresCount = 0;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private String trackingNumber;

    @Column(nullable = false)
    private boolean assigned;

    @Enumerated(EnumType.STRING)
    private ParcelStatus status;

    protected ParcelJpaEntity() {}

    public ParcelJpaEntity(Long weightId, String weightName, BigDecimal weightPrice,
                           Long dimensionsId, String dimensionsName, BigDecimal dimensionsPrice,
                           String contents, String trackingNumber, ParcelStatus status) {
        this.weightId = weightId;
        this.weightName = weightName;
        this.weightPrice = weightPrice;
        this.dimensionsId = dimensionsId;
        this.dimensionsName = dimensionsName;
        this.dimensionsPrice = dimensionsPrice;
        this.contents = contents;
        this.trackingNumber = trackingNumber;
        this.status = status;
    }

    public Long getId() { return id; }

    public String getContents() { return contents; }
    public String getTrackingNumber() { return trackingNumber; }
    public boolean isAssigned() { return assigned; }
    public ParcelStatus getStatus() { return status; }
    public Long getWeightId() { return weightId; }
    public String getWeightName() { return weightName; }
    public BigDecimal getWeightPrice() { return weightPrice; }
    public Long getDimensionsId() { return dimensionsId; }
    public String getDimensionsName() { return dimensionsName; }
    public BigDecimal getDimensionsPrice() { return dimensionsPrice; }
    public int getFailuresCount() { return failuresCount; }


    public void setContents(String contents) { this.contents = contents; }
    public void setStatus(ParcelStatus status) { this.status = status; }
    public void setAssigned(boolean assigned) { this.assigned = assigned; }
    public void setId(Long id) { this.id = id; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public void setWeightId(Long weightId) { this.weightId = weightId; }
    public void setWeightName(String weightName) { this.weightName = weightName; }
    public void setWeightPrice(BigDecimal weightPrice) { this.weightPrice = weightPrice; }
    public void setDimensionsId(Long dimensionsId) { this.dimensionsId = dimensionsId; }
    public void setDimensionsName(String dimensionsName) { this.dimensionsName = dimensionsName; }
    public void setDimensionsPrice(BigDecimal dimensionsPrice) { this.dimensionsPrice = dimensionsPrice; }
    public void setFailuresCount(int count) { this.failuresCount = count; }
}
