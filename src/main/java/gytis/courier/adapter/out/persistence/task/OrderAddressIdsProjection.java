package gytis.courier.adapter.out.persistence.task;

public interface OrderAddressIdsProjection {
    Long getSenderAddressId();
    Long getRecipientAddressId();
}
