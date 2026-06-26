package gytis.courier.application.service.person;

import gytis.courier.application.port.in.person.CourierCommandUseCase;
import gytis.courier.application.port.out.person.CourierCommandPort;
import gytis.courier.domain.person.Courier;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourierCommandService implements CourierCommandUseCase {
    private final CourierCommandPort port;

    public CourierCommandService(CourierCommandPort port) {
        this.port = port;
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
    }

    @Override
    @Transactional
    public void deactivate(Long courierId) {
        Courier courier = findById(courierId);
        courier.completeTask();
        update(courier);
    }
}
