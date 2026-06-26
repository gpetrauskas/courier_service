package gytis.courier.application.event;

import gytis.courier.application.port.out.DomainEventPublisher;
import gytis.courier.domain.event.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpringDomainEventPublisher  implements DomainEventPublisher {
    private final ApplicationEventPublisher publisher;

    public SpringDomainEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publish(DomainEvent event) {
        publisher.publishEvent(event);
    }

    @Override
    public void publish(List<DomainEvent> events) {
        for (DomainEvent event : events) {
            publisher.publishEvent(event);
        }
    }
}
