package com.example.courier.specification.task;

import com.example.courier.domain.Task;
import com.example.courier.dto.request.task.DeliveryTaskFilterDTO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecificationBuilder {
    private final DeliveryTaskSpecification deliveryTaskSpecification;

    public TaskSpecificationBuilder(DeliveryTaskSpecification deliveryTaskSpecification) {
        this.deliveryTaskSpecification = deliveryTaskSpecification;
    }

    public Specification<Task> buildTaskSpecification(DeliveryTaskFilterDTO dto) {
        return deliveryTaskSpecification.build(dto);
    }

    public Specification<Task> buildSpecificationCanBeCanceled(Long id) {
        return deliveryTaskSpecification.ifTaskActive(id).and(deliveryTaskSpecification.canBeCanceled());
    }
}
