package com.example.courier.service.notification.strategy;

import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.service.notification.NotificationTarget;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class NotificationStrategyResolver {
    private final Map<Class<? extends NotificationTarget>, NotificationDeliveryStrategy> strategyMap;

    public NotificationStrategyResolver(List<NotificationDeliveryStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        NotificationDeliveryStrategy::getSupportedType, s -> s
                ));
    }

    public NotificationDeliveryStrategy findStrategy(NotificationTarget target) {
        NotificationDeliveryStrategy strategy = strategyMap.get(target.getClass());
        if (strategy == null) {
            throw new ResourceNotFoundException("No strategy found for " + target.getClass().getSimpleName());
        }

        return strategy;
    }
}
