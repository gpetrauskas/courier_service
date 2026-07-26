package gytis.courier.adapter.out.persistence.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import gytis.courier.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class OutboxItemProcessor {
    private final Logger logger = LoggerFactory.getLogger(OutboxItemProcessor.class);
    private final OutboxJpaRepository repository;
    private final ApplicationEventPublisher publisher;
    private final ObjectMapper mapper;

    public OutboxItemProcessor(OutboxJpaRepository repository, ApplicationEventPublisher publisher, ObjectMapper mapper) {
        this.repository = repository;
        this.publisher = publisher;
        this.mapper = mapper;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void process(OutboxJpaEntity entity) {
        try {
            DomainEvent event = deserializeEvent(entity);
            publisher.publishEvent(event);

            entity.setStatus(OutboxEnum.COMPLETED);
            entity.setProcessed_at(LocalDateTime.now());
        } catch (Exception e) {
            logger.error("FAILED to process outbox event id {}, type {}", entity.getId(), entity.getEventType(), e);
            entity.setRetryCount(entity.getRetryCount() + 1);
            if (entity.getRetryCount() >= 3) {
                entity.setStatus(OutboxEnum.FAILED);
            }
        }

        repository.save(entity);
    }

    private DomainEvent deserializeEvent(OutboxJpaEntity entity) throws Exception{
        return mapper.readValue(entity.getPayload(), DomainEvent.class);
    }
}
