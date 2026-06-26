package gytis.courier.application.port.out.person;

import gytis.courier.domain.person.Courier;

import java.util.Optional;

public interface CourierCommandPort {
    Optional<Courier> findById(Long id);
    void update(Courier courier);
}
