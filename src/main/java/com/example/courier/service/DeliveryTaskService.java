package com.example.courier.service;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.TaskType;
import com.example.courier.domain.DeliveryTask;
import com.example.courier.dto.DeliveryTaskDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.mapper.DeliveryTaskMapper;
import com.example.courier.repository.DeliveryTaskRepository;
import com.example.courier.specification.DeliveryTaskSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@PreAuthorize("hasRole('ADMIN')")
@Service
public class DeliveryTaskService {
    @Autowired
    private DeliveryTaskRepository deliveryTaskRepository;
    @Autowired
    private DeliveryTaskMapper deliveryTaskMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public PaginatedResponseDTO<DeliveryTaskDTO> getAllDeliveryLists(
            int page, int size, Long courierId, Long taskListId, TaskType tType, DeliveryStatus status
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Specification<DeliveryTask> specification = DeliveryTaskSpecification.filterTasks(courierId, taskListId, tType, status, false);

        Page<DeliveryTask> taskPage = deliveryTaskRepository.findAll(specification, pageable);

        List<DeliveryTaskDTO> taskDTOS = taskPage.getContent().stream()
                .map(deliveryTaskMapper::toDeliveryTaskDTO)
                .toList();

        return new PaginatedResponseDTO<>(taskDTOS, taskPage.getNumber(), taskPage.getTotalElements(), taskPage.getTotalPages());    }

    @PreAuthorize("hasRole('ADMIN')")
    public void changeTaskStatus(Long taskId, DeliveryStatus newStatus) {

    }

}
