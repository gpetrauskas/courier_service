package gytis.courier.adapter.out.persistence.task.query;

import gytis.courier.adapter.out.persistence.task.TaskJpaRepository;
import gytis.courier.application.port.out.task.TaskQueryPort;
import gytis.courier.application.result.TaskCourierInfo;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TaskQueryAdapter implements TaskQueryPort {
    private final TaskJpaRepository taskJpaRepository;

    public TaskQueryAdapter(TaskJpaRepository taskJpaRepository) {
        this.taskJpaRepository = taskJpaRepository;
    }

    @Override
    public Optional<TaskCourierInfo> findCourierInfoByParcelId(Long parcelId) {
        return taskJpaRepository.findCourierInfoByParcelId(parcelId)
                .map(i -> new TaskCourierInfo(
                        i.getId(),
                        i.getCourierId(),
                        i.getItemId()
                ));
    }

/*    @Override
    public boolean existsActiveByCourierId(Long courierId) {
        return taskJpaRepository.existsByCourierIdAndDeliveryStatusNotIn(
                courierId,
                List.of(DeliveryStatus.COMPLETED, DeliveryStatus.CANCELED)
        );
    }*/
}
