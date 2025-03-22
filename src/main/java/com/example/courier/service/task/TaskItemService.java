package com.example.courier.service.task;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.Task;
import com.example.courier.domain.TaskItem;
import com.example.courier.domain.Order;
import com.example.courier.domain.Parcel;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.TaskItemRepository;
import com.example.courier.specification.TaskItemSpecification;
import com.example.courier.util.AuthUtils;
import com.example.courier.validation.TaskItemValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskItemService {

    private static final Logger logger = LoggerFactory.getLogger(TaskItemService.class);
    private final TaskItemRepository taskItemRepository;

    public TaskItemService(TaskItemRepository taskItemRepository) {
        this.taskItemRepository = taskItemRepository;
    }

    public List<TaskItem> createTaskItems(List<Parcel> parcels, List<Order> orders, Task task) {
        return parcels.stream()
                .map(parcel -> {
                    TaskItem taskItem = new TaskItem();
                    taskItem.setParcel(parcel);
                    taskItem.getParcel().setAssigned(true);
                    taskItem.setStatus(parcel.getStatus());

                    Order order = orders.stream()
                            .filter(o -> o.getParcelDetails().equals(parcel))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Order for parcel was not found"));

                    taskItem.setSenderAddress(order.getSenderAddress());
                    taskItem.setRecipientAddress(order.getRecipientAddress());
                    taskItem.setDeliveryPreference(order.getDeliveryMethod());
                    taskItem.setTask(task);

                    return taskItem;
                })
                .toList();
    }

    @Transactional
    public void removeItemFromTask(Long taskId, Long itemId) {
        Specification<TaskItem> specification = TaskItemSpecification.isActiveTaskItem(itemId);

        TaskItem taskItem = taskItemRepository.findOne(specification)
                .orElseThrow(() -> new ResourceNotFoundException("No active Task Item was found with id: " + itemId));

        Task task = taskItem.getTask();
        TaskItemValidator.validateItemCanBeRemovedFromTask(task, itemId);

        taskItem.getParcel().setAssigned(false);
        taskItem.setStatus(ParcelStatus.REMOVED_FROM_THE_LIST);

        checkIfNotLastItemInTask(task, taskItem);

        taskItemRepository.save(taskItem);
    }

    private void checkIfNotLastItemInTask(Task task, TaskItem item) {
        List<ParcelStatus> statusesPreventingRemoval = ParcelStatus.getStatusesPreventingRemoval();
        boolean hasRemainingItems = task.getItems().stream()
                .anyMatch(item1 -> !statusesPreventingRemoval.contains(item1.getStatus()));

        if (!hasRemainingItems) {
            item.getParcel().setAssigned(false);
            item.getTask().setDeliveryStatus(DeliveryStatus.CANCELED);
            Long adminId = AuthUtils.getAuthenticatedPersonId();
            item.getTask().setCanceledByAdminId(adminId);
            item.getTask().getCourier().setHasActiveTask(false);
        }
    }

    public void updateStatus(Long id, String newStatus) {
        TaskItem item = taskItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task Item not found"));
        ParcelStatus.isValidStatusChange(item.getTask().getTaskType(), ParcelStatus.valueOf(newStatus));
        Long personId = AuthUtils.getAuthenticatedPersonId();
        if (item.getTask().getCourier().getId().equals(personId)) {
            item.setStatus(ParcelStatus.valueOf(newStatus));
            item.getNotes().add("Status changed by Courier: " + personId + " to " + newStatus +
                    " for item id: " + id);
            saveAll(List.of(item));
        }
    }


    public void saveAll(List<TaskItem> items) {
        taskItemRepository.saveAll(items);
    }
}
