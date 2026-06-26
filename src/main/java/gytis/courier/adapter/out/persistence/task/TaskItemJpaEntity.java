package gytis.courier.adapter.out.persistence.task;

import gytis.courier.adapter.out.persistence.address.orderaddress.OrderAddressJpaEntity;
import gytis.courier.adapter.out.persistence.parcel.ParcelJpaEntity;
import gytis.courier.domain.order.ParcelStatus;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "delivery_task_items")
public class TaskItemJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private TaskJpaEntity task;

    @Column(name = "parcel_id", nullable = false)
    private Long parcelId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParcelStatus parcelStatus;

    @Column(name = "sender_address_id", nullable = false)
    private Long senderAddressId;

    @Column(name = "recipient_address_id", nullable = false)
    private Long recipientAddressId;

    @Column(name = "delivery_method_name")
    private String deliveryMethodName;

    @Column(name = "contents")
    private String contents;

    @ElementCollection
    @CollectionTable(name = "delivery_task_item_notes", joinColumns = @JoinColumn(name = "task_item_id"))
    @Column(name = "note")
    private Set<String> notes = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_address_id", insertable = false, updatable = false)
    private OrderAddressJpaEntity senderAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_address_id", insertable = false, updatable = false)
    private OrderAddressJpaEntity recipientAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parcel_id", insertable = false, updatable = false)
    private ParcelJpaEntity parcel;

    protected TaskItemJpaEntity() {}

    public Long getId() { return id; }
    public TaskJpaEntity getTask() { return task; }
    public Long getParcelId() { return parcelId; }
    public ParcelStatus getParcelStatus() { return parcelStatus; }
    public Long getSenderAddressId() { return senderAddressId; }
    public Long getRecipientAddressId() { return recipientAddressId; }
    public String getDeliveryMethodName() { return deliveryMethodName; }
    public String getContents() { return contents; }
    public Set<String> getNotes() { return notes; }

    public void setTask(TaskJpaEntity task) { this.task = task; }
    public void setParcelId(Long parcelId) { this.parcelId = parcelId; }
    public void setParcelStatus(ParcelStatus parcelStatus) { this.parcelStatus = parcelStatus; }
    public void setSenderAddressId(Long senderAddressId) { this.senderAddressId = senderAddressId; }
    public void setRecipientAddressId(Long recipientAddressId) { this.recipientAddressId = recipientAddressId; }
    public void setDeliveryMethodName(String deliveryMethodName) { this.deliveryMethodName = deliveryMethodName; }
    public void setContents(String contents) { this.contents = contents; }
    public void setNotes(Set<String> notes) { this.notes = notes; }

    public OrderAddressJpaEntity getSenderAddress() { return senderAddress; }
    public OrderAddressJpaEntity getRecipientAddress() { return recipientAddress; }
    public ParcelJpaEntity getParcel() { return parcel; }
}
