package gytis.courier.application.port.in.task;

import java.util.List;

public interface ParcelAssignmentFacade {
    void assignParcels(List<Long> parcelIds);
    void unassignParcels(List<Long> parcelIds);
}
