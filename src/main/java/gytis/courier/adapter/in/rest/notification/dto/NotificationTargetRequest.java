package gytis.courier.adapter.in.rest.notification.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NotificationTargetRequest.Broadcast.class, name = "BROADCAST"),
        @JsonSubTypes.Type(value = NotificationTargetRequest.Individual.class, name = "INDIVIDUAL")
})
public sealed interface NotificationTargetRequest permits NotificationTargetRequest.Broadcast, NotificationTargetRequest.Individual {
    record Broadcast(@NotNull String type) implements NotificationTargetRequest {}
    record Individual(@NotNull Long personId) implements NotificationTargetRequest {}
}
