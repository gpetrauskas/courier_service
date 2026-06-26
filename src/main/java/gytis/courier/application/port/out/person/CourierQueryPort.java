package gytis.courier.application.port.out.person;

import gytis.courier.application.readmodel.person.CourierReadModel;

import java.util.List;

public interface CourierQueryPort {
    List<CourierReadModel> findAvailableCouriers();
    Long getAvailableCouriersCount();
}
