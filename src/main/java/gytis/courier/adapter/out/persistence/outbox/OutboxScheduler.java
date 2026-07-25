package gytis.courier.adapter.out.persistence.outbox;

import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OutboxScheduler {
    private final OutboxJpaRepository repository;
    private final OutboxItemProcessor outboxItemProcessor;

    public OutboxScheduler(OutboxJpaRepository repository, OutboxItemProcessor outboxItemProcessor) {
        this.repository = repository;
        this.outboxItemProcessor = outboxItemProcessor;
    }

    @Scheduled(fixedRate = 10000)
    public void execute() {
        var list = repository.findByStatusOrderByCreatedAt(OutboxEnum.PENDING, PageRequest.of(0, 100));

        for (OutboxJpaEntity outboxJpa : list) {
            outboxItemProcessor.process(outboxJpa);
        }
    }

}
