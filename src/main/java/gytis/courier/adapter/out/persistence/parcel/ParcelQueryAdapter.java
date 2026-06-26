package gytis.courier.adapter.out.persistence.parcel;

import gytis.courier.application.port.out.parcel.ParcelQueryPort;
import gytis.courier.domain.order.ParcelStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ParcelQueryAdapter implements ParcelQueryPort {
    private final ParcelJpaRepository repository;

    public ParcelQueryAdapter(ParcelJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<ParcelStatus> findStatusByTrackingNumber(String trackingNumber) {
        return repository.findByTrackingNumber(trackingNumber);
    }

    @Override
    public Long getCountByStatusAndNotAssigned(ParcelStatus status) {
        return repository.countByStatusAndAssignedFalse(status);
    }

    @Override
    public Long test(List<ParcelStatus> statuses) {
        return repository.countByAssignedFalseAndStatusIn(statuses);
    }
}
