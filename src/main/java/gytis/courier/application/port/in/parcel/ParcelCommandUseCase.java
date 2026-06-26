package gytis.courier.application.port.in.parcel;

import gytis.courier.domain.task.ParcelStatusUpdate;

import java.util.List;

public interface ParcelCommandUseCase {
    void handleTaskCompleted(List<ParcelStatusUpdate> successes, List<Long> failures);
}
