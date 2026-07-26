package gytis.courier.application.service.person;

import gytis.courier.application.port.in.activityLog.ActivityLogUseCase;
import gytis.courier.application.port.in.person.CourierCommandUseCase;
import gytis.courier.application.port.out.person.CourierCommandPort;
import gytis.courier.domain.person.Courier;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourierCommandService implements CourierCommandUseCase {
    private final CourierCommandPort port;
    private final ActivityLogUseCase logUseCase;

    public CourierCommandService(CourierCommandPort port, ActivityLogUseCase logUseCase) {
        this.port = port;
        this.logUseCase = logUseCase;
    }

    public Courier findById(Long id) {
        return port.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Courier not found"));
    }

    public void update(Courier courier) {
        port.update(courier);
    }

    @Override
    @Transactional
    public void activate(Long courierId) {
        Courier courier = findById(courierId);
        courier.activateTask();
        update(courier);

        logUseCase.saveLog("ADMIN", "courier assign", "Courier#" + courierId + " was assigned to a new task");
    }

    @Override
    @Transactional
    public void deactivate(Long courierId) {
        Courier courier = findById(courierId);
        courier.completeTask();
        update(courier);

        logUseCase.saveLog("ADMIN", "courier assign", "Courier#" + courierId + " was unassigned from a task");
    }
}
