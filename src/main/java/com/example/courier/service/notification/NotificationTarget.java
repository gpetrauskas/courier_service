package com.example.courier.service.notification;

import com.example.courier.common.NotificationTargetType;

public sealed interface NotificationTarget {
    record BroadCast(NotificationTargetType type) implements NotificationTarget {}
    record Individual(Long personId) implements NotificationTarget {}
}
