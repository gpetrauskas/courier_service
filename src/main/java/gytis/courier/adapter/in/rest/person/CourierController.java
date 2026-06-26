package gytis.courier.adapter.in.rest.person;

import gytis.courier.application.port.in.person.CourierQueryUseCase;
import gytis.courier.application.readmodel.person.CourierReadModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/couriers")
@PreAuthorize("hasRole('ADMIN')")
public class CourierController {
    private final CourierQueryUseCase courierQuery;

    public CourierController(CourierQueryUseCase courierQuery) {
        this.courierQuery = courierQuery;
    }

    @GetMapping("/available")
    public List<CourierReadModel> availableCouriers() {
        return courierQuery.getAvailableCouriers();
    }

    @GetMapping("/available/count")
    public ResponseEntity<Long> availableCouriersCount() {
        return ResponseEntity.ok(courierQuery.getAvailableCouriersCount());
    }
}
