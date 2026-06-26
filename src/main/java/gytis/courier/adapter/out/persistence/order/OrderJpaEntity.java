package gytis.courier.adapter.out.persistence.order;

import gytis.courier.adapter.out.persistence.address.orderaddress.OrderAddressJpaEntity;
import gytis.courier.adapter.out.persistence.parcel.ParcelJpaEntity;
import gytis.courier.adapter.out.persistence.person.user.UserJpaEntity;
import gytis.courier.domain.order.OrderStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class OrderJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "sender_address_id", nullable = false)
    private OrderAddressJpaEntity senderAddress;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "recipient_address_id", nullable = false)
    private OrderAddressJpaEntity recipientAddress;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "parcel_id")
    private ParcelJpaEntity parcel;

    @Column(name = "delivery_method_id")
    private Long deliveryMethodId;

    @Column(name = "delivery_method_name")
    private String deliveryMethodName;

    @Column(name = "delivery_method_description")
    private String deliveryMethodDescription;

    @Column(name = "delivery_method_price")
    private BigDecimal deliveryMethodPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserJpaEntity user;

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public OrderAddressJpaEntity getSenderAddress() { return senderAddress; }
    public OrderAddressJpaEntity getRecipientAddress() { return recipientAddress; }
    public ParcelJpaEntity getParcel() { return parcel; }
    public Long getDeliveryMethodId() { return deliveryMethodId; }
    public String getDeliveryMethodName() { return deliveryMethodName; }
    public String getDeliveryMethodDescription() { return deliveryMethodDescription; }
    public BigDecimal getDeliveryMethodPrice() { return deliveryMethodPrice; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getCreateDate() { return createDate; }
    public UserJpaEntity getUser() { return user; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setSenderAddress(OrderAddressJpaEntity senderAddress) { this.senderAddress = senderAddress; }
    public void setRecipientAddress(OrderAddressJpaEntity recipientAddress) { this.recipientAddress = recipientAddress; }
    public void setParcel(ParcelJpaEntity parcel) { this.parcel = parcel; }
    public void setDeliveryMethodId(Long deliveryMethodId) { this.deliveryMethodId = deliveryMethodId; }
    public void setDeliveryMethodName(String deliveryMethodName) { this.deliveryMethodName = deliveryMethodName; }
    public void setDeliveryMethodDescription(String description) { this.deliveryMethodDescription = description; }
    public void setDeliveryMethodPrice(BigDecimal deliveryMethodPrice) { this.deliveryMethodPrice = deliveryMethodPrice; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setCreateDate(LocalDateTime createDate) { this.createDate = createDate; }
}
