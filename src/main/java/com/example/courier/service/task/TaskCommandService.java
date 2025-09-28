package com.example.courier.service.task;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.*;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.CreateTaskDTO;
import com.example.courier.dto.request.UpdateTaskItemNotesRequest;
import com.example.courier.dto.response.UpdateTaskItemNotesResponse;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.TaskRepository;
import com.example.courier.service.authorization.AuthorizationService;
import com.example.courier.service.notification.NotificationService;
import com.example.courier.service.order.query.OrderQueryService;
import com.example.courier.service.person.query.PersonLookupService;
import com.example.courier.service.security.CurrentPersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/** Command side service for mutating {@link Task} aggregates.
 */
@Service
public class TaskCommandService {
    private final Logger logger = LoggerFactory.getLogger(TaskCommandService.class);
    private final PersonLookupService personLookupService;
    private final TaskRepository repository;
    private final OrderQueryService orderQueryService;
    private final CurrentPersonService currentPersonService;
    private final AuthorizationService authorizationService;
    private final NotificationService notificationService;


    public TaskCommandService(PersonLookupService personLookupService, TaskRepository repository,
                              OrderQueryService orderQueryService, CurrentPersonService currentPersonService,
                              AuthorizationService authorizationService, NotificationService notificationService
    ) {
        this.personLookupService = personLookupService;
        this.repository = repository;
        this.orderQueryService = orderQueryService;
        this.currentPersonService = currentPersonService;
        this.authorizationService = authorizationService;
        this.notificationService = notificationService;
    }

    /**
     * Creates a new task and its items for a courier.
     *
     * <p>Also transits parcels to delivery state if the task type is not {@code PICKUP}.
     *
     * @param dto task creation details
     */
    public void initiateNewTask(CreateTaskDTO dto) {
        logger.info("Creating a task list for the courier: {}", dto.courierId());

        Courier courier = personLookupService.fetchPersonByIdAndType(dto.courierId(), Courier.class);
        Admin admin = currentPersonService.getCurrentPersonAs(Admin.class);
        List<Order> orderListWithParcelDetails = orderQueryService.getAllOrdersWithParcelByParcelIds(dto.parcelsIds());

        courier.activateTask();

        Task task = Task.create(dto.taskType(), courier, admin, orderListWithParcelDetails);

        repository.save(task);
    }

    /**
     * Cancels the given task and all its items.
     *
     * @param id task identifier
     * @throws ResourceNotFoundException if no task is found
     */
    public void cancel(Long id) {
        Task task = repository.findWithCourierItemsAndParcelsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No task found with id: " + id));

        task.cancelWithItems(currentPersonService.getCurrentPersonId());
        logger.info("Task with ID: {} was successfully canceled", task.getId());

        repository.save(task);
    }

    /**
     * Marks a courier as checked in for a task.
     *
     * <p>
     *     Validates courier assignment and triggers a notification.
     * </p>
     *
     * @param taskId task identifier
     * @throws ResourceNotFoundException if no task found
     */
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

    /**
     * Changes the task status as an admin.
     *
     * @param taskId task identifier
     * @param newStatus new status as string
     * @throws ResourceNotFoundException if task is not found
     * @throws IllegalArgumentException if task is not updatable for an admin
     */
    public void changeStatus(Long taskId, String newStatus) {
        DeliveryStatus nStatus = DeliveryStatus.validateAndGetStatus(newStatus);

        Task task = repository.findWithCourierItemsAndParcelsById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID " + taskId + " was not found"));

        task.changeStatusAsAdmin(nStatus, currentPersonService.getCurrentPersonId());
        logger.info("Task ID: {} status was successfully changed to: {}", task.getId(), nStatus);

        repository.save(task);
    }

    /**
     * Removes a task item from a given task.
     *
     * @param taskId task identifier
     * @param itemId item identifier
     * @return response indicating success
     * @throws ResourceNotFoundException if no task is found
     */
    public ApiResponseDTO removeTaskItemFromTask(Long taskId, Long itemId) {
        Long adminId = currentPersonService.getCurrentPersonId();
        Task task = repository.findWithDetailsById(taskId).orElseThrow(
                () -> new ResourceNotFoundException("Tak not found"));

        task.removeItem(itemId, adminId);

        repository.save(task);
        return new ApiResponseDTO("success", "Item was successfully removed");
    }


    /**
     * Updates the status of a task item.
     *
     * <p>
     *     Validates courier assignment unlesss the caller is admin.
     * </p>
     *
     * @param itemId item identifier
     * @param newStatus new status as a string
     * @param taskId task identifier
     * @return api response
     * @throws ResourceNotFoundException if no task is found
     */
    public ApiResponseDTO updateTaskItemStatus(Long itemId, String newStatus, Long taskId) {
        ParcelStatus status = ParcelStatus.valueOf(newStatus);
        Long courierId = currentPersonService.getCurrentPersonId();

        Task task = repository.findWithRelationsById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task was not found"));

        if (!currentPersonService.isAdmin()) {
            authorizationService.validateCourierTaskAssignment(task);
        }

        task.updateItemStatus(itemId, status, courierId);

        repository.save(task);

        return new ApiResponseDTO("success", "Task item status was changed successfully");
    }

    /**
     * Adds a note to the selected {@link TaskItem} within a {@link Task}.
     *
     * <p>Performs validation to make use the current user is assigned to the task.</p>
     *
     * @param request contains a note
     * @param itemId identifier of task item
     * @throws ResourceNotFoundException if task is not found
     * @throws IllegalArgumentException if note is invalid or in the final state
     */
    public UpdateTaskItemNotesResponse addItemNote(UpdateTaskItemNotesRequest request, Long itemId) {
        Task task = repository.findTaskByItemIdWithItems(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Task was not found"));

        authorizationService.validateCourierTaskAssignment(task);
        task.addTaskItemNote(request.note(), itemId);
        logger.info("Note was added to item: {} in a task: {}", itemId, task.getId());

        repository.save(task);

        return new UpdateTaskItemNotesResponse("Note was added successfully.", itemId);
    }

    /* Helper methods
    */
}
