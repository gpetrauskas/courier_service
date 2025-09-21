package com.example.courier.service.task;

import com.example.courier.domain.Task;
import com.example.courier.domain.TaskItem;
import com.example.courier.domain.Order;
import com.example.courier.dto.request.UpdateTaskItemNotesRequest;
import com.example.courier.dto.response.UpdateTaskItemNotesResponse;
import com.example.courier.service.task.command.TaskItemCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Task item service that is orchestrating task item operations.
 */
@Service
public class TaskItemService {

    private static final Logger logger = LoggerFactory.getLogger(TaskItemService.class);
    private final TaskItemCommandService commandService;

    public TaskItemService(TaskItemCommandService commandService) {
        this.commandService = commandService;
    }

    /**
     * Creates a new task items.
     *
     * <p> Delegates to {@link TaskItemCommandService#createTaskItems(List, Task)}</p>
     * */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public List<TaskItem> createTaskItems(List<Order> orders, Task task) {
        return commandService.createTaskItems(orders, task);
    }

    /**
     * Set a note for an task item.
     * 
     * <p> Delegates to {@link TaskItemCommandService#updateNotes(UpdateTaskItemNotesRequest, Long)}</p>*/
    @Transactional
    public UpdateTaskItemNotesResponse updateNote(UpdateTaskItemNotesRequest notesRequest, Long taskItemId) {
        return commandService.updateNotes(notesRequest, taskItemId);
    }
}
