package com.example.courier.service;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.DeliveryTask;
import com.example.courier.domain.DeliveryTaskItem;
import com.example.courier.dto.CourierTaskDTO;
import com.example.courier.dto.mapper.DeliveryTaskMapper;
import com.example.courier.dto.request.UpdateTaskItemNotesRequest;
import com.example.courier.dto.request.UpdateTaskItemStatusRequest;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.DeliveryTaskItemRepository;
import com.example.courier.repository.DeliveryTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourierService {

    private static final Logger logger = LoggerFactory.getLogger(CourierService.class);

    private final DeliveryTaskRepository deliveryTaskRepository;
    private final DeliveryTaskItemRepository deliveryTaskItemRepository;
    private final AuthorizationService authorizationService;

    public CourierService(DeliveryTaskRepository deliveryTaskRepository, DeliveryTaskItemRepository deliveryTaskItemRepository,
                          AuthorizationService authorizationService) {
        this.deliveryTaskRepository = deliveryTaskRepository;
        this.deliveryTaskItemRepository = deliveryTaskItemRepository;
        this.authorizationService = authorizationService;
    }

    @Transactional(readOnly = true)
    public List<CourierTaskDTO> getCurrentTaskList(Long courierId) {
        if (courierId == null) {
            throw new ResourceNotFoundException("Courier id is null");
        }
        List<DeliveryTask> list = deliveryTaskRepository.findByCourierIdAndDeliveryStatus(
                courierId, DeliveryStatus.IN_PROGRESS
        );

        return list.stream()
                .map(i -> DeliveryTaskMapper.INSTANCE.toCourierTaskDTO(i, i.getTaskType()))
                .toList();
    }

    @Transactional
    public void updateTaskItemNotes(Long taskItemId, UpdateTaskItemNotesRequest payLoad) {
        logger.info("Tryin to update task item with ID: {}", taskItemId);

        DeliveryTaskItem taskItem = validateAndFetchTaskItem(taskItemId);
        taskItem.getNotes().add(payLoad.note());

        deliveryTaskItemRepository.save(taskItem);
    }

    @Transactional
    public void updateTaskItemStatus(Long taskItemId, UpdateTaskItemStatusRequest payload) {
        logger.info("Trying to update task item status with ID: {}", taskItemId);

        DeliveryTaskItem taskItem = validateAndFetchTaskItem(taskItemId);
        ParcelStatus newStatus = ParcelStatus.valueOf(payload.status().toUpperCase());

        validateStatusChange(taskItem, newStatus);
        taskItem.setStatus(newStatus);

        deliveryTaskItemRepository.save(taskItem);

    }

    private void validateStatusChange(DeliveryTaskItem taskItem, ParcelStatus newStatus) {
        if (taskItem.getStatus() == ParcelStatus.DELIVERED ||
                taskItem.getStatus() == ParcelStatus.CANCELED ||
                taskItem.getStatus() == ParcelStatus.PICKED_UP ||
                taskItem.getStatus() == ParcelStatus.REMOVED_FROM_THE_LIST) {
            throw new IllegalStateException("Cannot update status for canceled or completed task item");
        }

        if (!ParcelStatus.isValidStatusChange(taskItem.getTask().getTaskType(), newStatus)) {
            throw new IllegalStateException("Invalid status change for task type: " + taskItem.getTask().getTaskType());
        }
    }

    private DeliveryTaskItem validateAndFetchTaskItem(Long taskItemId) {
        DeliveryTaskItem taskItem = deliveryTaskItemRepository.findById(taskItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item was not found"));

        checkIfTaskCanBeUpdated(taskItem.getTask());
        authorizationService.validateCourierTaskAssignment(taskItem);

        return taskItem;
    }

    private void checkIfTaskCanBeUpdated(DeliveryTask deliveryTask) {
        if (deliveryTask.getDeliveryStatus() == DeliveryStatus.COMPLETED ||
            deliveryTask.getDeliveryStatus() == DeliveryStatus.CANCELED) {
            throw new IllegalStateException("Cannot update notes for completed or canceled task");
        }
    }
}
