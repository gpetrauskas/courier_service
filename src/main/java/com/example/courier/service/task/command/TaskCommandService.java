package com.example.courier.service.task.command;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.TaskType;
import com.example.courier.domain.*;
import com.example.courier.dto.CreateTaskDTO;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.TaskRepository;
import com.example.courier.service.authorization.AuthorizationService;
import com.example.courier.service.notification.NotificationService;
import com.example.courier.service.order.query.OrderQueryService;
import com.example.courier.service.person.query.PersonLookupService;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.service.task.TaskItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskCommandService {
    private final Logger logger = LoggerFactory.getLogger(TaskCommandService.class);
    private final PersonLookupService personLookupService;
    private final TaskRepository repository;
    private final OrderQueryService orderQueryService;
    private final TaskItemService taskItemService;
    private final CurrentPersonService currentPersonService;
    private final AuthorizationService authorizationService;
    private final NotificationService notificationService;


    public TaskCommandService(PersonLookupService personLookupService, TaskRepository repository,
                              OrderQueryService orderQueryService, TaskItemService taskItemService, CurrentPersonService currentPersonService,
                              AuthorizationService authorizationService, NotificationService notificationService) {
        this.personLookupService = personLookupService;
        this.repository = repository;
        this.orderQueryService = orderQueryService;
        this.taskItemService = taskItemService;
        this.currentPersonService = currentPersonService;
        this.authorizationService = authorizationService;
        this.notificationService = notificationService;
    }


    @Transactional
    public void initiateNewTask(CreateTaskDTO dto) {
        logger.info("Creating a task list for the courier: {}", dto.courierId());

        Courier courier = personLookupService.fetchPersonByIdAndType(dto.courierId(), Courier.class);
        courier.activateTask();
        Admin admin = currentPersonService.getCurrentPersonAs(Admin.class);

        Task task = Task.create(dto.taskType(), courier, admin);

        List<Order> orderListWithParcelDetails = orderQueryService.getAllOrdersWithParcelByParcelIds(dto.parcelsIds());
        if (task.getTaskType() != TaskType.PICKUP) {
            orderListWithParcelDetails.forEach(o -> o.getParcelDetails().transitionToDelivery());
        }

        List<TaskItem> taskItems = taskItemService.createTaskItems(orderListWithParcelDetails, task);
        task.addTaskItems(taskItems);

        repository.save(task);
    }

    @Transactional
    public void cancel(Long id) {
        Task task = repository.findWithCourierItemsAndParcelsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No task found with id: " + id));

        task.cancelWithItems(currentPersonService.getCurrentPersonId());
        logger.info("Task with ID: {} was successfully canceled", task.getId());

        repository.save(task);
    }

    @Transactional
    @PreAuthorize("hasRole('COURIER')")
    public void checkIn(Long taskId) {
        logger.info("Courier Trying to check in. Task ID: {}", taskId);
        Task task = repository.findWithRelationsById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found."));

        logger.info("authorizing task assignment to courier");
        authorizationService.validateCourierTaskAssignment(task);

        task.completeOnCheckIn();
        repository.save(task);

        notificationService.notifyCourierCheckedIn(taskId, task.getCourier().getId());
        logger.info("Courier checked in: Task ID = {}, Courier ID = {}", taskId,task.getCourier().getId());
    }

    public void changeStatus(Long taskId, String newStatus) {
        DeliveryStatus nStatus = DeliveryStatus.validateAndGetStatus(newStatus);
        if (!DeliveryStatus.isAdminUpdatable(nStatus)) {
            throw new IllegalArgumentException("Task currently cannot be updated");
        }

        Task task = repository.findWithCourierItemsAndParcelsById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID " + taskId + " was not found"));

        task.changeStatusAsAdmin(nStatus, currentPersonService.getCurrentPersonId());
        logger.info("Task ID: {} status was successfully changed to: {}", task.getId(), nStatus);

        repository.save(task);
    }

    /* Helper methods
    */
}
