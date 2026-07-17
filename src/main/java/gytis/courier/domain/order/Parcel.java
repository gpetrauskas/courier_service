package gytis.courier.domain.order;

import gytis.courier.domain.task.TaskType;
import gytis.courier.domain.delivery.DeliveryOption;
import gytis.courier.domain.event.DomainEvent;
import gytis.courier.domain.event.ParcelMaxFailuresReachedEvent;
import jakarta.validation.ValidationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Parcel {
    private Long id;
    private Long weightId;
    private String weightName;
    private BigDecimal weightPrice;
    private Long dimensionsId;
    private String dimensionsName;
    private BigDecimal dimensionsPrice;
    private int failuresCount;
    private String contents;
    private String trackingNumber;
    private boolean isAssigned;
    private ParcelStatus status;
    private final List<DomainEvent> events = new ArrayList<>();

    private static final int MAX_FAILURE_COUNT = 3;

    protected Parcel() {}

    public static Parcel restore(
            Long id,
            Long weightId,
            String weightName,
            BigDecimal weightPrice,
            Long dimensionsId,
            String dimensionsName,
            BigDecimal dimensionsPrice,
            int failuresCount,
            String contents,
            String trackingNumber,
            boolean isAssigned,
            ParcelStatus status
    ) {
        Parcel p = new Parcel();
        p.id = id;
        p.weightId = weightId;
        p.weightName = weightName;
        p.weightPrice = weightPrice;
        p.dimensionsId = dimensionsId;
        p.dimensionsName = dimensionsName;
        p.dimensionsPrice = dimensionsPrice;
        p.failuresCount = failuresCount;
        p.contents = contents;
        p.trackingNumber = trackingNumber;
        p.isAssigned = isAssigned;
        p.status = status;
        return p;
    }

    public Parcel(DeliveryOption weight, DeliveryOption dimensions, String contents) {
        Objects.requireNonNull(weight);
        Objects.requireNonNull(dimensions);

        this.weightId = weight.id();
        this.weightName = weight.name();
        this.weightPrice = weight.price();

        this.dimensionsId = dimensions.id();
        this.dimensionsName = dimensions.name();
        this.dimensionsPrice = dimensions.price();

        this.contents = contents;
        this.trackingNumber = UUID.randomUUID().toString();
        this.isAssigned = false;
        this.status = ParcelStatus.WAITING_FOR_PAYMENT;
    }

    public Long getId() { return id; }
    public Long getWeightId() { return weightId; }
    public String getWeightName() { return weightName; }
    public BigDecimal getWeightPrice() { return weightPrice; }
    public Long getDimensionsId() { return dimensionsId; }
    public String getDimensionsName() { return dimensionsName; }
    public BigDecimal getDimensionsPrice() { return dimensionsPrice; }
    public String getContents() { return contents; }
    public int getFailuresCount() { return failuresCount; }
    public String getTrackingNumber() { return trackingNumber; }
    public boolean isAssigned() { return isAssigned; }
    public ParcelStatus getStatus() { return status; }
    public int getEventSize() {
        return this.events.size();
    }

    public String getWeightDisplayName() {
        return this.weightName.replace("_weight", "");
    }

    public String getDimensionsDisplayName() {
        return this.dimensionsName.replace("_size", "");
    }

    public void cancel() { this.status = ParcelStatus.CANCELED; }

    public void markAsPickingUp() {
        if (status != ParcelStatus.WAITING_FOR_PAYMENT) {
            throw new IllegalStateException("parcel cannot be picked up yet");
        }

        status = ParcelStatus.PICKING_UP;
    }

    public void unassign() {
        this.isAssigned = false;
    }

    public List<DomainEvent> pullEvents() {
        List<DomainEvent> eventsToReturn = new ArrayList<>(events);
        events.clear();
        return eventsToReturn;
    }

    public void assign() {
        if (isAssigned) {
            throw new IllegalStateException("Parcel is already assigned.");
        }
        this.isAssigned = true;
    }

    public void transitionToDelivery() {
        if (!ParcelStatus.PICKED_UP.equals(this.status)) {
            throw new IllegalStateException(
                    String.format("Cannot transition to DELIVERY from %s", this.status)
            );
        }

        this.status = ParcelStatus.DELIVERING;
    }

    public void failedDeliveryAttemptAdd() {
        this.failuresCount++;
        if (this.failuresCount >= MAX_FAILURE_COUNT) {
            events.add(new ParcelMaxFailuresReachedEvent(this.id, this.failuresCount));
        }
    }

    public void transitionIfNeeded(TaskType taskType) {
        if (!taskType.equals(TaskType.PICKUP)) {
            transitionToDelivery();
        }
    }

    public BigDecimal calculatePrice() {
        return weightPrice.add(dimensionsPrice);
    }

    public void changeStatus(ParcelStatus status) {
        if (this.status.isFinalState()) {
            throw new IllegalStateException("Status cannot be changed");
        }

        if (!this.status.isValidTransition(status)) {
            throw new ValidationException("invalid status transition: " + this.status + " -> " + status);
        }

        this.status = status;
    }

    public void changeContents(String newContent) {
        Objects.requireNonNull(newContent);
        this.contents = newContent;
    }

    public void updateSection(ParcelSectionUpdateCommand command) {
        Objects.requireNonNull(command);

        if (command.status() != null) {
            changeStatus(command.status());
        }
        if (command.contents() != null) {
            changeContents(command.contents());
        }
    }

    public void isAddressEditable() {
        if (this.status.isFinalState()) {
            throw new IllegalStateException("Parcel is not editable");
        }
    }
}
