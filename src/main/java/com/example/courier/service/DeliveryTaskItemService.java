package com.example.courier.service;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.DeliveryTask;
import com.example.courier.domain.DeliveryTaskItem;
import com.example.courier.domain.Order;
import com.example.courier.domain.Parcel;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.DeliveryTaskItemRepository;
import com.example.courier.specification.TaskItemSpecification;
import com.example.courier.util.AuthUtils;
import com.example.courier.validation.TaskItemValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeliveryTaskItemService {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryTaskItemService.class);
    private final DeliveryTaskItemRepository deliveryTaskItemRepository;

    public DeliveryTaskItemService(DeliveryTaskItemRepository deliveryTaskItemRepository) {
        this.deliveryTaskItemRepository = deliveryTaskItemRepository;
    }

    public List<DeliveryTaskItem> createTaskItems(List<Parcel> parcels, List<Order> orders, DeliveryTask deliveryTask) {

        return parcels.stream()
                .map(parcel -> {
                    DeliveryTaskItem taskItem = new DeliveryTaskItem();
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
                    taskItem.setTask(deliveryTask);

                    return taskItem;
                })
                .toList();
    }

    @Transactional
    public void removeItemFromTask(Long taskId, Long itemId) {
        Specification<DeliveryTaskItem> specification = TaskItemSpecification.isActiveTaskItem(itemId);

        DeliveryTaskItem taskItem = deliveryTaskItemRepository.findOne(specification)
                .orElseThrow(() -> new ResourceNotFoundException("No active Task Item was found with id: " + itemId));

        DeliveryTask deliveryTask = taskItem.getTask();
        TaskItemValidator.validateItemCanBeRemovedFromTask(deliveryTask, itemId);

        taskItem.getParcel().setAssigned(false);
        taskItem.setStatus(ParcelStatus.REMOVED_FROM_THE_LIST);

        checkIfNotLastItemInTask(deliveryTask, taskItem);

        deliveryTaskItemRepository.save(taskItem);

    }

    private void checkIfNotLastItemInTask(DeliveryTask task, DeliveryTaskItem item) {
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



    public void saveAll(List<DeliveryTaskItem> items) {
        deliveryTaskItemRepository.saveAll(items);
    }
}
