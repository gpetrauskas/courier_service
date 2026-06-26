package gytis.courier.application.service.task;

import gytis.courier.application.port.in.task.ParcelAssignmentFacade;
import gytis.courier.application.port.out.parcel.ParcelCommandPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParcelAssignmentFacadeImpl implements ParcelAssignmentFacade {
    private final ParcelCommandPort commandPort;

    public ParcelAssignmentFacadeImpl(ParcelCommandPort commandPort) {
        this.commandPort = commandPort;
    }


    @Override
    public void assignParcels(List<Long> parcelIds) {
        int updated = commandPort.markAssigned(parcelIds);

        if (updated != parcelIds.size()) {
            throw new IllegalStateException("Some parcels could not be assigned");
        }
    }

    @Override
    public void unassignParcels(List<Long> parcelIds) {
        int updated = commandPort.markUnassigned(parcelIds);

        if (updated != parcelIds.size()) {
            throw new IllegalStateException("Some parcels could not be unassigned");
        }
    }
}
