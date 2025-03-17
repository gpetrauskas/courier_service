package com.example.courier.service;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.common.TaskType;
import com.example.courier.domain.*;
import com.example.courier.dto.CreateTaskDTO;
import com.example.courier.dto.DeliveryTaskDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.mapper.DeliveryTaskMapper;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.DeliveryTaskRepository;
import com.example.courier.service.order.OrderService;
import com.example.courier.service.parcel.ParcelService;
import com.example.courier.service.person.PersonService;
import com.example.courier.specification.DeliveryTaskSpecification;
import com.example.courier.util.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PreAuthorize("hasRole('ADMIN')")
@Service
public class DeliveryTaskService {
    private static final Logger log = LoggerFactory.getLogger(DeliveryTaskService.class);
    private final DeliveryTaskRepository deliveryTaskRepository;
    private final DeliveryTaskMapper deliveryTaskMapper;
    private final ParcelService parcelService;
    private final PersonService personService;
    private final OrderService orderService;
    private final DeliveryTaskItemService deliveryTaskItemService;

    public DeliveryTaskService(DeliveryTaskRepository deliveryTaskRepository, DeliveryTaskMapper deliveryTaskMapper,
                               ParcelService parcelService, PersonService personService, OrderService orderService,
                               DeliveryTaskItemService deliveryTaskItemService) {
        this.deliveryTaskRepository = deliveryTaskRepository;
        this.deliveryTaskMapper = deliveryTaskMapper;
        this.parcelService = parcelService;
        this.personService = personService;
        this.orderService = orderService;
        this.deliveryTaskItemService = deliveryTaskItemService;
    }

    @Transactional
    public void createNewDeliveryTask(CreateTaskDTO createTaskDTO) {
        Courier courier = personService.fetchPersonByIdAndType(createTaskDTO.courierId(), Courier.class);
        personService.hasCourierActiveTask(courier);
        Admin admin = AuthUtils.getAuthenticated(Admin.class);
        List<Parcel> parcels = parcelService.fetchParcelsByIdBatch(createTaskDTO.parcelsIds());
        List<Order> orders = orderService.fetchAllByParcelDetails(parcels);

        DeliveryTask deliveryTask = new DeliveryTask();
        deliveryTask.setCourier(courier);
        deliveryTask.setTaskType(createTaskDTO.taskType()
                .equalsIgnoreCase("PICKING_UP") ?
                TaskType.PICKUP : TaskType.DELIVERY);
        deliveryTask.setCreatedBy(admin);
        deliveryTask.setDeliveryStatus(DeliveryStatus.IN_PROGRESS);

        deliveryTaskRepository.save(deliveryTask);

        List<DeliveryTaskItem> deliveryTaskItems = deliveryTaskItemService.createTaskItems(parcels, orders, deliveryTask);
        deliveryTask.setItems(deliveryTaskItems);

        courier.setHasActiveTask(true);
        personService.save(courier);

        deliveryTask.setItems(deliveryTaskItems);
        deliveryTaskItemService.saveAll(deliveryTaskItems);
    }

    public Map<String, Long> getAvailableItemsCount() {
        List<ParcelStatus> statuses = List.of(ParcelStatus.PICKING_UP, ParcelStatus.DELIVERING);

        Map<String, Long> response = statuses.stream()
                .collect(Collectors.toMap(
                        status -> status.name().toLowerCase(),
                        parcelService::getAvailableItemsCountByStatus
                ));
        log.info("Items count fetched successfully {}", response);
        return response;
    }

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

    public void cancel(Long id) {
        Specification<DeliveryTask> specification = DeliveryTaskSpecification.ifTaskActive(id)
                .and(DeliveryTaskSpecification.canBeCanceled());
        DeliveryTask deliveryTask = deliveryTaskRepository.findOne(specification)
                .orElseThrow(() -> new ResourceNotFoundException("No such Task with id: " + id));

        deliveryTask.getItems()
                .forEach(item -> {
                    if (!ParcelStatus.getStatusesPreventingRemoval().contains(item.getStatus())) {
                        item.setStatus(ParcelStatus.CANCELED);
                        item.getParcel().setAssigned(false);
                    }
                });

        boolean allItemsCanceled = deliveryTask.getItems().stream()
                .allMatch(item -> item.getStatus() == ParcelStatus.CANCELED);

        if (allItemsCanceled) {
            Long adminId = AuthUtils.getAuthenticatedPersonId();
            deliveryTask.getCourier().setHasActiveTask(false);
            deliveryTask.setDeliveryStatus(DeliveryStatus.CANCELED);
            deliveryTask.setCanceledByAdminId(adminId);
            deliveryTaskRepository.save(deliveryTask);
        }
    }





    @PreAuthorize("hasRole('ADMIN')")
    public void changeTaskStatus(Long taskId, DeliveryStatus newStatus) {

    }
}
