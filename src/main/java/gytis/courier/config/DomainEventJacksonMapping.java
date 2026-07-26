package gytis.courier.config;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import gytis.courier.domain.event.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "eventType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PaymentConfirmedEvent.class, name = "PaymentConfirmedEvent"),
        @JsonSubTypes.Type(value = CourierChangeEvent.class, name = "CourierChangeEvent"),
        @JsonSubTypes.Type(value = CourierCheckedInEvent.class, name = "CourierCheckedInEvent"),
        @JsonSubTypes.Type(value = CourierReturningEvent.class, name = "CourierReturningEvent"),
        @JsonSubTypes.Type(value = OrderAddressUpdatedEvent.class, name = "OrderAddressUpdatedEvent"),
        @JsonSubTypes.Type(value = OrderCanceledEvent.class, name = "OrderCanceledEvent"),
        @JsonSubTypes.Type(value = ParcelMaxFailuresReachedEvent.class, name = "ParcelMaxFailuresReachedEvent"),
        @JsonSubTypes.Type(value = TaskAssignedEvent.class, name = "TaskAssignedEvent"),
        @JsonSubTypes.Type(value = TaskCanceledEvent.class, name = "TaskCanceledEvent"),
        @JsonSubTypes.Type(value = TaskCompletedEvent.class, name = "TaskCompletedEvent"),
        @JsonSubTypes.Type(value = PaymentFailedEvent.class, name = "PaymentFailedEvent")
})
public interface DomainEventJacksonMapping {
}
