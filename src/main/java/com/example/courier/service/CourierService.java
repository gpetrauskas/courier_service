package com.example.courier.service;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.domain.DeliveryTask;
import com.example.courier.domain.DeliveryTaskItem;
import com.example.courier.dto.CourierTaskDTO;
import com.example.courier.dto.mapper.DeliveryTaskMapper;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.exception.UnauthorizedAccessException;
import com.example.courier.repository.DeliveryTaskItemRepository;
import com.example.courier.repository.DeliveryTaskRepository;
import com.example.courier.util.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CourierService {

    private static final Logger logger = LoggerFactory.getLogger(CourierService.class);

    @Autowired
    private DeliveryTaskRepository deliveryTaskRepository;
    @Autowired
    private DeliveryTaskItemRepository deliveryTaskItemRepository;

    public List<CourierTaskDTO> getCurrentTaskList(Long courierId) {
        if (courierId == null) {
            throw new ResourceNotFoundException("Courier id is null");
        }
        List<DeliveryTask> list = deliveryTaskRepository.findByCourierIdAndDeliveryStatus(courierId, DeliveryStatus.IN_PROGRESS);

        return list.stream()
                .map(i -> DeliveryTaskMapper.INSTANCE.toCourierTaskDTO(i, i.getTaskType()))
                .toList();
    }

    public void checkAndUpdateTaskItem(Long taskItemId, Map<String, String> payLoad) {
        DeliveryTaskItem taskItem = deliveryTaskItemRepository.findById(taskItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Task item not found with id: " + taskItemId));

        DeliveryTask deliveryTask = taskItem.getTask();

        if (deliveryTask.getDeliveryStatus() != DeliveryStatus.CANCELED ||
                deliveryTask.getDeliveryStatus() != DeliveryStatus.COMPLETED) {

            Long authenticatedCourierId = AuthUtils.getAuthenticatedPersonId();

            if (deliveryTask.getCourier().getId().equals(authenticatedCourierId)) {
                updateTaskItemNotes(payLoad, taskItem);
            } else {
                throw new UnauthorizedAccessException("Not authorized to update this task item");
            }
        } else {
            throw new IllegalStateException("Cannot update update notes for canceled or completed tasks");
        }
    }

    private void updateTaskItemNotes(Map<String, String> payLoad, DeliveryTaskItem taskItem) {
        String newNote = payLoad.get("note");

        List<String> notes = taskItem.getNotes();
        notes.add(newNote);
        taskItem.setNotes(notes);

        deliveryTaskItemRepository.save(taskItem);
    }

}
