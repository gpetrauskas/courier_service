package com.example.courier.service.task;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.common.TaskType;
import com.example.courier.domain.*;
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
import com.example.courier.service.NotificationService;
import com.example.courier.service.authorization.AuthorizationService;
import com.example.courier.service.order.OrderService;
import com.example.courier.service.parcel.ParcelService;
import com.example.courier.service.person.PersonService;
import com.example.courier.service.person.PersonServiceImpl;
import com.example.courier.specification.task.TaskSpecificationBuilder;
import com.example.courier.util.AuthUtils;
import com.example.courier.util.PageableUtils;
import com.example.courier.validation.task.TaskValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@PreAuthorize("hasRole('ADMIN')")
@Service
public class TaskService {
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;
    private final DeliveryTaskMapper deliveryTaskMapper;
    private final ParcelService parcelService;
    private final PersonServiceImpl<Courier> personServiceImpl;
    private final OrderService orderService;
    private final TaskItemService taskItemService;
    private final TaskSpecificationBuilder specificationBuilder;
    private final AuthorizationService authorizationService;
    private final TaskValidator taskValidator;
    private final NotificationService notificationService;
    @Autowired
    private PersonService personService;

    public TaskService(TaskRepository taskRepository, DeliveryTaskMapper deliveryTaskMapper,
                       ParcelService parcelService, PersonServiceImpl personServiceImpl, OrderService orderService,
                       TaskItemService taskItemService, TaskSpecificationBuilder specificationBuilder,
                       AuthorizationService authorizationService, TaskValidator taskValidator,
                       NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.deliveryTaskMapper = deliveryTaskMapper;
        this.parcelService = parcelService;
        this.personServiceImpl = personServiceImpl;
        this.orderService = orderService;
        this.taskItemService = taskItemService;
        this.specificationBuilder = specificationBuilder;
        this.authorizationService = authorizationService;
        this.taskValidator = taskValidator;
        this.notificationService = notificationService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void createNewDeliveryTask(CreateTaskDTO createTaskDTO) {
        Courier courier = personServiceImpl.findById(createTaskDTO.courierId())
                .orElseThrow(() -> new ResourceNotFoundException("Courier not found"));
        personServiceImpl.hasCourierActiveTask(courier);
        Admin admin = AuthUtils.getAuthenticated(Admin.class);

        List<Parcel> parcels = parcelService.fetchParcelsByIdBatch(createTaskDTO.parcelsIds());
        List<Order> orders = orderService.fetchAllByParcelDetails(parcels);

        Task task = new Task();
        task.initiateTaskCreation(createTaskDTO, courier, admin, parcels, orders);
        taskRepository.save(task);

        List<TaskItem> taskItems = taskItemService.createTaskItems(parcels, orders, task);
        task.addTaskItems(taskItems);

        courier.setHasActiveTask(true);
        personServiceImpl.save(courier);

        task.setItems(taskItems);
        taskItemService.saveAll(taskItems);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COURIER')")
    public PaginatedResponseDTO<? extends TaskBase> getAllTaskLists(DeliveryTaskFilterDTO dto) {
        Pageable pageable = PageableUtils.toPageable(dto);
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

        if (!task.areAllItemsCanceled()) {
            log.error("Task cannot be canceled because some items are still active. Task ID: {}", task.getId());
            throw new TaskNotCancelableException("There are active items. Task cannot be canceled");
        }

        Long adminId = AuthUtils.getAuthenticatedPersonId();
        task.cancel(adminId);

        taskRepository.save(task);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void changeTaskStatus(Long taskId, DeliveryStatus newStatus) {
        if (!DeliveryStatus.isAdminUpdatable(newStatus)) {
            throw new IllegalArgumentException("Task status cannot be updated.");
        }

        taskValidator.validateAdminUpdatable();

        Task task = fetchTaskById(taskId);
        task.setDeliveryStatus(newStatus);
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

        notificationService.notifyAdmin(taskId, task.getCourier().getId());
        log.info("Courier checked in: Task ID = {}, Courier ID = {}", taskId,task.getCourier().getId());
    }

    private Task fetchTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task was not found with ID: " + id));
    }

}
