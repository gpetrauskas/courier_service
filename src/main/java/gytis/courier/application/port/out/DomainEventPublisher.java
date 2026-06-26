package gytis.courier.application.port.out;

import gytis.courier.domain.event.DomainEvent;

import java.util.List;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
    void publish(List<DomainEvent> events);
}
