package gytis.courier.application.port.in.person;

import gytis.courier.application.readmodel.person.CourierReadModel;

import java.util.List;

public interface CourierQueryUseCase {
    List<CourierReadModel> getAvailableCouriers();
    Long getAvailableCouriersCount();
}
