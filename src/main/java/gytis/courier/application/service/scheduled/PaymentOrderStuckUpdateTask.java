package gytis.courier.application.service.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PaymentOrderStuckUpdateTask {
    private static final Logger log = LoggerFactory.getLogger(PaymentOrderStuckUpdateTask.class);

    @Scheduled(fixedRate = 300000)
    public void updateStuckOrder() {

    }

}
