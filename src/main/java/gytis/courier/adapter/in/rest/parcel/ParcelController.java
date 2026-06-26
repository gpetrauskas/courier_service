package gytis.courier.adapter.in.rest.parcel;

import gytis.courier.application.port.in.parcel.ParcelQueryUseCase;
import gytis.courier.application.readmodel.parcel.AvailableParcelsCountReadModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parcel")
public class ParcelController {
    private final ParcelQueryUseCase useCase;

    public ParcelController(ParcelQueryUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/trackOrder/{trackingNumber}")
    public ResponseEntity<String> getParcelStatus(@PathVariable String trackingNumber) {
        return ResponseEntity.ok(useCase.track(trackingNumber).toString().toUpperCase());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/availableCount")
    public AvailableParcelsCountReadModel availableParcelsCount() {
        System.out.println("cia");
        return useCase.availableParcelsCount();
    }
}
