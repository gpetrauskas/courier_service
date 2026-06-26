package gytis.courier.adapter.out.strategy.notification;

import gytis.courier.application.port.out.notification.NotificationDeliveryPort;
import gytis.courier.domain.notification.Notification;
import gytis.courier.domain.notification.NotificationTarget;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class NotificationStrategyResolver implements NotificationDeliveryPort {
    private final Map<Class<? extends NotificationTarget>, NotificationDeliveryStrategy> strategies;

    public NotificationStrategyResolver(List<NotificationDeliveryStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(NotificationDeliveryStrategy::getSupportedType, s -> s));
    }

    @Override
    public void deliver(Notification notification) {
        NotificationDeliveryStrategy strategy = strategies.get(notification.getTarget().getClass());
        if (strategy == null) {
            throw new IllegalStateException( "No strategy found for target type: " + notification.getTarget().getClass());
        }
        strategy.deliver(notification);
    }
}
