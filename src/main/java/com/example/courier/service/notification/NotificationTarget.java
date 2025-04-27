package com.example.courier.service.notification;

import com.example.courier.common.NotificationTargetType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NotificationTarget.BroadCast.class, name = "BROADCAST"),
        @JsonSubTypes.Type(value = NotificationTarget.Individual.class, name = "INDIVIDUAL")
})
public sealed interface NotificationTarget {
    record BroadCast(NotificationTargetType type) implements NotificationTarget {}
    record Individual(Long personId) implements NotificationTarget {}
}
