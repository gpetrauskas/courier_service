package gytis.courier.adapter.out.persistence.outbox;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox")
public class OutboxJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private OutboxEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime processed_at;
    private int retryCount = 0;

    public OutboxJpaEntity() {}

    public OutboxJpaEntity(String eventType, String payload) {
        this.eventType = eventType;
        this.payload = payload;
        this.status = OutboxEnum.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public OutboxEnum getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public void setStatus(OutboxEnum status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getProcessed_at() {
        return processed_at;
    }

    public void setProcessed_at(LocalDateTime processed_at) {
        this.processed_at = processed_at;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}
