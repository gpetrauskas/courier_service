package gytis.courier.domain.order;

import gytis.courier.domain.delivery.DeliveryOption;

import gytis.courier.domain.event.OrderAddressUpdatedEvent;
import gytis.courier.domain.event.OrderCanceledEvent;
import jakarta.validation.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Order {
    private final Long id;
    private final Long userId;
    private final OrderAddress senderAddress;
    private final OrderAddress recipientAddress;
    private final Parcel parcel;
    private Long deliveryMethodId;
    private String deliveryMethodName;
    private String deliveryMethodDescription;
    private BigDecimal deliveryMethodPrice;
    private OrderStatus status;
    private final LocalDateTime createDate;

    public Order(Long id, Long userId, OrderAddress senderAddress, OrderAddress recipientAddress,
                 Parcel parcel, Long deliveryMethodId, String deliveryMethodName, String deliveryMethodDescription,
                 BigDecimal deliveryMethodPrice, OrderStatus status, LocalDateTime createDate) {
        this.id = id;
        this.userId = userId;
        this.senderAddress = senderAddress;
        this.recipientAddress = recipientAddress;
        this.parcel = parcel;
        this.deliveryMethodId = deliveryMethodId;
        this.deliveryMethodName = deliveryMethodName;
        this.deliveryMethodDescription = deliveryMethodDescription;
        this.deliveryMethodPrice = deliveryMethodPrice;
        this.status = status;
        this.createDate = createDate;
    }

    public static Order create(Long userId, OrderAddress senderAddress, OrderAddress recipientAddress,
                               Parcel parcel, DeliveryOption deliveryMethod) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(senderAddress);
        Objects.requireNonNull(recipientAddress);
        Objects.requireNonNull(parcel);
        Objects.requireNonNull(deliveryMethod);
        return new Order(null, userId, senderAddress, recipientAddress, parcel, deliveryMethod.id(),
                deliveryMethod.name(), deliveryMethod.description(), deliveryMethod.price(), OrderStatus.PENDING, LocalDateTime.now());
    }

    public static Order restore(Long id, Long userId, OrderAddress sender, OrderAddress recipient,
                                Parcel parcel, Long deliveryMethodId, String deliveryMethodName,
                                String deliveryMethodDescription, BigDecimal deliveryMethodPrice,
                                OrderStatus status, LocalDateTime createDate) {

        return new Order(id, userId, sender, recipient, parcel, deliveryMethodId,
                deliveryMethodName, deliveryMethodDescription, deliveryMethodPrice, status, createDate);
    }

    public Order withId(Long id) {
        return new Order(id, userId, senderAddress, recipientAddress, parcel, deliveryMethodId, deliveryMethodName,
                deliveryMethodDescription, deliveryMethodPrice, status, createDate);
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public OrderAddress getSenderAddress() { return senderAddress; }
    public OrderAddress getRecipientAddress() { return recipientAddress; }
    public Parcel getParcel() { return parcel; }
    public Long getDeliveryMethodId() { return deliveryMethodId; }
    public String getDeliveryMethodName() { return deliveryMethodName; }
    public String getDeliveryMethodDescription() { return deliveryMethodDescription; }
    public BigDecimal getDeliveryMethodPrice() { return deliveryMethodPrice; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getCreateDate() { return createDate; }

    public BigDecimal calculateShippingCost() {
        return deliveryMethodPrice.add(parcel.calculatePrice());
    }

    public void updateDeliveryMethodPreference(DeliveryOption preference) {
        Objects.requireNonNull(preference);
        validateUpdatable();

        if (!this.deliveryMethodId.equals(preference.id())) {
            this.deliveryMethodId = preference.id();
            this.deliveryMethodName = preference.name();
            this.deliveryMethodDescription = preference.description();
            this.deliveryMethodPrice = preference.price();
        }
    }

    public OrderCanceledEvent cancel() {
        validateUpdatable();

        this.status = OrderStatus.CANCELED;
        parcel.cancel();

        return new OrderCanceledEvent(id);
    }

    public boolean isOwnedBy(Long id) {
        return userId.equals(id);
    }

    public void updateStatus(OrderStatus newStatus) {
        Objects.requireNonNull(newStatus);
        validateUpdatable();

        if (newStatus == OrderStatus.COMPLETED) {
            throw new ValidationException("Order confirmation happens automatically via payment");
        }

        if (newStatus != this.status && this.status == OrderStatus.PENDING) {
            this.status = newStatus;
        }
    }

    public void updateParcelStatus(ParcelStatus newStatus) {
        Objects.requireNonNull(newStatus);
        validateUpdatable();

        parcel.changeStatus(newStatus);
    }

    public void changeParcelContents(String contents) {
        Objects.requireNonNull(contents);
        parcel.changeContents(contents);
    }

    public void markConfirmed() {
        validateUpdatable();

        this.status = OrderStatus.CONFIRMED;
        parcel.markAsPickingUp();
    }

    public Optional<OrderAddressUpdatedEvent> updateAddress(OrderAddressSectionUpdateCommand command) {
        this.parcel.isAddressEditable();

        OrderAddress updatedAddress = (Objects.equals(command.selectedAddress(), "senderSection"))
                ? this.senderAddress
                : this.recipientAddress;

        updatedAddress.update(command);

        return parcel.isAssigned()
                ? Optional.of(new OrderAddressUpdatedEvent(this.parcel.getId(), command.selectedAddress()))
                : Optional.empty();
    }

    public void validateUpdatable() {
        if (this.status.isFinalState()) {
            throw new ValidationException("Order is in final state and cannot be updated");
        }
    }

    public void updateParcelSection(ParcelSectionUpdateCommand command) {
        validateUpdatable();

        this.parcel.updateSection(command);
    }
}
