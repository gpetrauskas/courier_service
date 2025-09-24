package com.example.courier.service.task.command;

import com.example.courier.domain.Order;
import com.example.courier.domain.Task;
import com.example.courier.domain.TaskItem;
import com.example.courier.dto.request.UpdateTaskItemNotesRequest;
import com.example.courier.dto.response.UpdateTaskItemNotesResponse;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.TaskItemRepository;
import com.example.courier.service.authorization.AuthorizationService;
import com.example.courier.service.security.CurrentPersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Command side service for mutating {@link TaskItem} entities.
 *
 * <p>
 *     Provide operations for creating task items from order/parcel details and updating notes.
 * </p>
 */
@Service
public class TaskItemCommandService {
    private final static Logger logger = LoggerFactory.getLogger(TaskItemCommandService.class);
    private final TaskItemRepository repository;
    private final AuthorizationService authorizationService;
    private final CurrentPersonService currentPersonService;

    public TaskItemCommandService(TaskItemRepository repository, AuthorizationService authorizationService, CurrentPersonService currentPersonService) {
        this.repository = repository;
        this.authorizationService = authorizationService;
        this.currentPersonService = currentPersonService;
    }

    /**
     * Creates {@link TaskItem} instances for the given orders.
     *
     * <p> Each task item is initialized with parcel and order details and linked to parent task.</p>
     *
     * @param orders list of orders and parcels details to convert into task items
     * @param task parent task
     * @return list of created task items
     */
    public List<TaskItem> createTaskItems(List<Order> orders, Task task) {
        return orders.stream()
                .map(o -> TaskItem.create(o.getParcelDetails(), o, task))
                .toList();
    }

    /**
     * Add a note to a task item.
     *
     * <p> Validates that current courier is assigned to the task before updating.</p>
     *
     * @param request note update request
     * @param taskItemId identifier of the task item
     * @return response containing confirmation and the task item id
     * @throws ResourceNotFoundException if task item is not found
     */
    public UpdateTaskItemNotesResponse updateNotes(UpdateTaskItemNotesRequest request, Long taskItemId) {
        TaskItem item = repository.findTaskItemWithTaskAndCourierById(taskItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Task item not found"));

        authorizationService.validateCourierTaskAssignmentByTaskItem(item);
        item.addNote(request.note());
        logger.info("Courier {} added note to TaskItem {}", currentPersonService.getCurrentPersonId(), taskItemId);

        repository.save(item);
        return new UpdateTaskItemNotesResponse("Note added successfully", taskItemId);
    }
}
