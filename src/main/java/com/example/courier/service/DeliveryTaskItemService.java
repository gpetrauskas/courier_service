package com.example.courier.service;

import com.example.courier.domain.DeliveryTask;
import com.example.courier.domain.DeliveryTaskItem;
import com.example.courier.domain.Order;
import com.example.courier.domain.Parcel;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.DeliveryTaskItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryTaskItemService {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryTaskItemService.class);
    private final DeliveryTaskItemRepository deliveryTaskItemRepository;

    public DeliveryTaskItemService(DeliveryTaskItemRepository deliveryTaskItemRepository) {
        this.deliveryTaskItemRepository = deliveryTaskItemRepository;
    }

    public List<DeliveryTaskItem> createTaskItems(List<Parcel> parcels, List<Order> orders, DeliveryTask deliveryTask) {

        List<DeliveryTaskItem> taskItems = parcels.stream()
                .map(parcel -> {
                    DeliveryTaskItem taskItem = new DeliveryTaskItem();
                    taskItem.setParcel(parcel);
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

        deliveryTaskItemRepository.saveAll(taskItems);
        return taskItems;
    }

    public void saveAll(List<DeliveryTaskItem> items) {
        deliveryTaskItemRepository.saveAll(items);
    }
}
