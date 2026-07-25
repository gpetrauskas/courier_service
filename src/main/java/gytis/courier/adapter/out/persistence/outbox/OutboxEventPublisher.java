package gytis.courier.adapter.out.persistence.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gytis.courier.application.port.out.DomainEventPublisher;
import gytis.courier.domain.event.DomainEvent;
import gytis.courier.domain.event.OutboxEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxEventPublisher implements DomainEventPublisher {
    private final OutboxJpaRepository repository;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher publisher;

    public OutboxEventPublisher(OutboxJpaRepository repository, ObjectMapper objectMapper, ApplicationEventPublisher publisher) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.publisher = publisher;
    }

    @Override
    public void publish(DomainEvent event) {
        if (event instanceof OutboxEvent) {
            this.saveToOutbox(event);
        } else {
            publisher.publishEvent(event);
        }
    }

    @Override
    public void publish(List<DomainEvent> events) {
        for (DomainEvent event : events) {
            this.publish(event);
        }
    }

    private void saveToOutbox(DomainEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);

            OutboxJpaEntity entity = new OutboxJpaEntity(
                    event.getClass().getSimpleName(),
                    json
            );

            repository.save(entity);
        } catch (JsonProcessingException e) {
            System.out.println("error while saving to outbox, " + e.getMessage());
        }
    }
}
