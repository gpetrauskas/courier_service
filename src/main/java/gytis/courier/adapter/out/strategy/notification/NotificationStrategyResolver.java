package gytis.courier.adapter.out.strategy.notification;

import gytis.courier.application.port.out.auth.PersonQueryPort;
import gytis.courier.application.port.out.notification.NotificationDeliveryPort;
import gytis.courier.domain.notification.Notification;
import gytis.courier.domain.notification.NotificationTarget;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class NotificationStrategyResolver implements NotificationDeliveryPort {
    private final Map<Class<? extends NotificationTarget>, NotificationDeliveryStrategy> strategies;
    private final SimpMessagingTemplate template;
    private final PersonQueryPort personQueryPort;

    public NotificationStrategyResolver(List<NotificationDeliveryStrategy> strategies, SimpMessagingTemplate template, PersonQueryPort personQueryPort) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(NotificationDeliveryStrategy::getSupportedType, s -> s));
        this.template = template;
        this.personQueryPort = personQueryPort;
    }

    @Override
    public void deliver(Notification notification) {
        NotificationDeliveryStrategy strategy = strategies.get(notification.getTarget().getClass());
        if (strategy == null) {
            throw new IllegalStateException( "No strategy found for target type: " + notification.getTarget().getClass());
        }

        switch (notification.getTarget()) {
            case NotificationTarget.Broadcast b -> template.convertAndSend("/topic/notifications/" + b.type().name(), notification);
            case NotificationTarget.Individual i -> template.convertAndSendToUser(getEmail(i.personId()), "/queue/notifications", notification);
        }

        strategy.deliver(notification);
    }

    private String getEmail(Long id) {
        return personQueryPort.getEmailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("email not found"));
    }
}
