package com.example.courier.service.task;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.NotificationTargetType;
import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.*;
import com.example.courier.dto.request.notification.NotificationRequestDTO;
import com.example.courier.dto.request.task.DeliveryTaskFilterDTO;
import com.example.courier.dto.response.task.AdminTaskDTO;
import com.example.courier.dto.response.task.CourierTaskDTO;
import com.example.courier.dto.CreateTaskDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.mapper.DeliveryTaskMapper;
import com.example.courier.dto.response.task.TaskBase;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.exception.TaskNotCancelableException;
import com.example.courier.repository.TaskRepository;
import com.example.courier.service.notification.NotificationService;
import com.example.courier.service.authorization.AuthorizationService;
import com.example.courier.service.notification.NotificationTarget;
import com.example.courier.service.order.OrderService;
import com.example.courier.service.parcel.ParcelService;
import com.example.courier.service.person.PersonService;
import com.example.courier.specification.task.TaskSpecificationBuilder;
import com.example.courier.util.AuthUtils;
import com.example.courier.util.PageableUtils;
import com.example.courier.validation.TaskItemValidator;
import com.example.courier.validation.task.TaskValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@PreAuthorize("hasRole('ADMIN')")
@Service
public class TaskService {
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;
    private final DeliveryTaskMapper deliveryTaskMapper;
    private final ParcelService parcelService;
    private final PersonService personService;
    private final OrderService orderService;
    private final TaskItemService taskItemService;
    private final TaskSpecificationBuilder specificationBuilder;
    private final AuthorizationService authorizationService;
    private final TaskValidator taskValidator;
    private final NotificationService notificationService;
    private final TaskItemValidator taskItemValidator;

    public TaskService(TaskRepository taskRepository, DeliveryTaskMapper deliveryTaskMapper,
                       ParcelService parcelService, PersonService personService, OrderService orderService,
                       TaskItemService taskItemService, TaskSpecificationBuilder specificationBuilder,
                       AuthorizationService authorizationService, TaskValidator taskValidator,
                       NotificationService notificationService, TaskItemValidator taskItemValidator) {
        this.taskRepository = taskRepository;
        this.deliveryTaskMapper = deliveryTaskMapper;
        this.parcelService = parcelService;
        this.personService = personService;
        this.orderService = orderService;
        this.taskItemService = taskItemService;
        this.specificationBuilder = specificationBuilder;
        this.authorizationService = authorizationService;
        this.taskValidator = taskValidator;
        this.notificationService = notificationService;
        this.taskItemValidator = taskItemValidator;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void createNewDeliveryTask(CreateTaskDTO createTaskDTO) {
        log.info("Creating new delivery task for courier {}", createTaskDTO.courierId());
        Courier courier = personService.fetchPersonByIdAndType(createTaskDTO.courierId(), Courier.class);

        List<Parcel> parcels = parcelService.fetchParcelsByIdBatch(createTaskDTO.parcelsIds());
        List<Order> orders = orderService.fetchAllByParcelDetails(parcels);

        parcels.forEach(parcel -> {
            if (parcel.getStatus() == ParcelStatus.PICKED_UP) {
                parcel.transitionToDelivery();
            }
        });

        taskValidator.validateCreation(createTaskDTO, courier, parcels);

        Task task = new Task();
        task.initiateTaskCreation(createTaskDTO, courier, AuthUtils.getAuthenticated(Admin.class));
        task.addTaskItems(taskItemService.createTaskItems(parcels, orders, task));
        taskRepository.save(task);

        courier.setHasActiveTask(true);

        personService.save(courier);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COURIER')")
    public PaginatedResponseDTO<? extends TaskBase> getAllTaskLists(DeliveryTaskFilterDTO dto) {
        Pageable pageable = PageableUtils.createPageable(dto.page(), dto.size(), dto.sortBy(), dto.direction().toString());
        if (!AuthUtils.isAdmin()) {
            return getCourierHistory(pageable);
        }

        Specification<Task> specification = specificationBuilder.buildTaskSpecification(dto);
        Page<Task> taskPage = taskRepository.findAll(specification, pageable);

        List<AdminTaskDTO> paginatedResponseDTO = taskPage.getContent().stream()
                .map(deliveryTaskMapper::toDeliveryTaskDTO)
                .toList();

        return new PaginatedResponseDTO<>(paginatedResponseDTO, taskPage.getNumber(),
                taskPage.getTotalElements(), taskPage.getTotalPages());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void cancel(Long id) {
        Specification<Task> specification = specificationBuilder.buildSpecificationCanBeCanceled(id);
        Task task = taskRepository.findOne(specification)
                .orElseThrow(() -> new ResourceNotFoundException("No such Task with id: " + id));

        task.cancelItems();

        if (!task.isAllItemsCanceled()) {
            log.error("Task cannot be canceled because some items are still active. Task ID: {}", task.getId());
            throw new TaskNotCancelableException("There are active items. Task cannot be canceled");
        }

        Long adminId = AuthUtils.getAuthenticatedPersonId();
        task.cancel(adminId);

        taskRepository.save(task);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void changeTaskStatus(Long taskId, String newStatus) {
        log.info("Changing tak {} status to {}", taskId, newStatus);
        DeliveryStatus status = DeliveryStatus.valueOf(newStatus);
        Task task = fetchTaskById(taskId);
        taskValidator.validateAdminUpdatable(task);

        if (status.equals(DeliveryStatus.COMPLETED)) {
            task.completeTask();
        } else if (status.equals(DeliveryStatus.CANCELED)) {
           cancel(taskId);
        } else {
            throw new IllegalStateException("No such status.");
        }

        taskRepository.save(task);
    }

    @PreAuthorize("hasRole('COURIER')")
    public List<CourierTaskDTO> getCourierCurrentTask() {
        Long id = AuthUtils.getAuthenticatedPersonId();
        Set<DeliveryStatus> currentStatuses = DeliveryStatus.currentStatuses();
        List<Task> taskList = taskRepository.findByCourierIdAndDeliveryStatusIn(id, currentStatuses);

        return taskList.stream()
                .map(task -> deliveryTaskMapper.toCourierTaskDTO(task, task.getTaskType()))
                .toList();
    }

    private PaginatedResponseDTO<CourierTaskDTO> getCourierHistory(Pageable pageable) {
        Long courierId = AuthUtils.getAuthenticatedPersonId();
        Page<Task> taskList = taskRepository.findByCourierIdAndDeliveryStatusIn(
                courierId, DeliveryStatus.historicalStatuses(), pageable);

        return new PaginatedResponseDTO<>(taskList.stream()
                .map(task -> deliveryTaskMapper.toCourierTaskDTO(task, task.getTaskType()))
                .toList(), taskList.getNumber(), taskList.getTotalElements(), taskList.getTotalPages());
    }

    @PreAuthorize("hasRole('COURIER')")
    @Transactional
    public void checkIn(Long taskId) {
        log.info("Courier Trying to check in. Task ID: {}", taskId);
        Task task = taskRepository.findWithRelationsById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found."));
        log.info("authorizing task assignment to courier");
        authorizationService.validateCourierTaskAssignment(task);
        task.completeOnCheckIn();
        taskRepository.save(task);

        NotificationRequestDTO notificationMessage = new NotificationRequestDTO(
                String.format("Courier %d CheckedIn", task.getCourier().getId()),
                String.format("Courier checked in: Task ID = %d, Courier ID = %d", taskId,task.getCourier().getId()),
                new NotificationTarget.BroadCast(NotificationTargetType.ADMIN)
        );

        notificationService.createNotification(notificationMessage);
        log.info("Courier checked in: Task ID = {}, Courier ID = {}", taskId,task.getCourier().getId());
    }

    private Task fetchTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task was not found with ID: " + id));
    }

}
