package gytis.courier.application.service.person;

import gytis.courier.application.port.in.person.CourierQueryUseCase;
import gytis.courier.application.port.out.person.CourierQueryPort;
import gytis.courier.application.readmodel.person.CourierReadModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourierQueryService implements CourierQueryUseCase {
    private final CourierQueryPort port;

    public CourierQueryService(CourierQueryPort port) {
        this.port = port;
    }

    @Override
    public List<CourierReadModel> getAvailableCouriers() {
        return port.findAvailableCouriers();
    }

    @Override
    public Long getAvailableCouriersCount() {
        return port.getAvailableCouriersCount();
    }
}
