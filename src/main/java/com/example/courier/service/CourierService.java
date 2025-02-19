package com.example.courier.service;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.domain.DeliveryTask;
import com.example.courier.domain.DeliveryTaskItem;
import com.example.courier.dto.CourierTaskDTO;
import com.example.courier.dto.mapper.DeliveryTaskMapper;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.DeliveryTaskItemRepository;
import com.example.courier.repository.DeliveryTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CourierService {

    private static final Logger logger = LoggerFactory.getLogger(CourierService.class);

    @Autowired
    private DeliveryTaskRepository deliveryTaskRepository;
    @Autowired
    private DeliveryTaskItemRepository deliveryTaskItemRepository;
    @Autowired
    private AuthorizationService authorizationService;

    public List<CourierTaskDTO> getCurrentTaskList(Long courierId) {
        if (courierId == null) {
            throw new ResourceNotFoundException("Courier id is null");
        }
        List<DeliveryTask> list = deliveryTaskRepository.findByCourierIdAndDeliveryStatus(courierId, DeliveryStatus.IN_PROGRESS);

        return list.stream()
                .map(i -> DeliveryTaskMapper.INSTANCE.toCourierTaskDTO(i, i.getTaskType()))
                .toList();
    }

    @Transactional
    public void checkAndUpdateTaskItem(Long taskItemId, Map<String, String> payLoad) {
        logger.info("Tryin to update task item with ID: {}", taskItemId);

        if (payLoad == null || !payLoad.containsKey("note") ||
            payLoad.get("note") == null || payLoad.get("note").trim().isEmpty()) {
            throw new IllegalArgumentException("note is required for updating.");
        }

        DeliveryTaskItem taskItem = deliveryTaskItemRepository.findById(taskItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Task item not found with id: " + taskItemId));

        checkIfTaskCanBeUpdated(taskItem.getTask());

        authorizationService.validateCourierTaskAssignment(taskItem);

        updateTaskItemNotes(payLoad, taskItem);
    }

    private void checkIfTaskCanBeUpdated(DeliveryTask deliveryTask) {
        if (deliveryTask.getDeliveryStatus() == DeliveryStatus.COMPLETED ||
            deliveryTask.getDeliveryStatus() == DeliveryStatus.CANCELED) {
            throw new IllegalStateException("Cannot update notes for completed or canceled task");
        }
    }

    private void updateTaskItemNotes(Map<String, String> payLoad, DeliveryTaskItem taskItem) {
        String newNote = Optional.ofNullable(payLoad.get("note"))
                .orElseThrow(() -> new IllegalArgumentException("Note cannot be null"));

        List<String> notes = taskItem.getNotes();
        notes.add(newNote);
        taskItem.setNotes(notes);

        deliveryTaskItemRepository.save(taskItem);
    }

}
