package com.example.courier.service.task;

import com.example.courier.domain.*;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.task.DeliveryTaskFilterDTO;
import com.example.courier.dto.response.task.CourierTaskDTO;
import com.example.courier.dto.CreateTaskDTO;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.response.task.TaskBase;
import com.example.courier.service.task.command.TaskCommandService;
import com.example.courier.service.task.query.TaskQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Task service that is orchestrating task operations.
 *
 * <p>
 *     Acts as the transactional boundary for task commands and queries,
 *     delegating to {@link TaskCommandService} and {@link TaskQueryService}.
 * </p>
 */
@PreAuthorize("hasAnyRole('ADMIN', 'COURIER')")
@Service
public class TaskService {
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskCommandService commandService;
    private final TaskQueryService queryService;

    public TaskService(TaskCommandService commandService, TaskQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    /** Creates new delivery task and its items.
     *
     * <p> Delegates to {@link TaskCommandService#initiateNewTask(CreateTaskDTO)}. </p>
     */
    @Transactional
    public void createNewDeliveryTask(CreateTaskDTO createTaskDTO) {
        commandService.initiateNewTask(createTaskDTO);
    }

    /** Return paginated list of tasks matching the given filter.
     *
     * <p> Delegates to {@link TaskQueryService#getAllTasksList(DeliveryTaskFilterDTO)}. </p>
     */
    @Transactional(readOnly = true)
    public PaginatedResponseDTO<? extends TaskBase> getAllTaskLists(DeliveryTaskFilterDTO dto) {
        return queryService.getAllTasksList(dto);
    }

    /** Cancel task and its task items.
     *
     * <p> Delegates to {@link TaskCommandService#cancel(Long)}. </p>
     */
    @Transactional
    public void cancel(Long id) {
        commandService.cancel(id);
    }

    /** Change task status.
     *
     * <p> Delegates to {@link TaskCommandService#changeStatus(Long, String)}. </p>
     */
    @Transactional
    public void changeTaskStatus(Long taskId, String newStatus) {
        commandService.changeStatus(taskId, newStatus);
    }

    /** Get couriers currently active tasks.
     *
     * <p> Delegates to {@link TaskQueryService#getCurrentCourierTask()}. </p>
     */
    @Transactional(readOnly = true)
    public List<CourierTaskDTO> getCourierCurrentTask() {
        return queryService.getCurrentCourierTask();
    }

    /** Check-in a courier.
     *
     * <p> Delegates to {@link TaskCommandService#checkIn(Long)}. </p>
     * */
    @Transactional
    public void checkIn(Long taskId) {
        commandService.checkIn(taskId);
    }

    /** Removes an item from a task.
     *
     * <p> Delegates to {@link TaskCommandService#removeTaskItemFromTask(Long, Long)}. </p>
     * */
    @Transactional
    public ApiResponseDTO removeItem(Long taskId, Long itemId) {
        return commandService.removeTaskItemFromTask(taskId, itemId);
    }

    /** Update item status.
     *
     * <p> Delegates to {@link TaskCommandService#updateTaskItemStatus(Long, String, Long)}. </p>
     * */
    @Transactional
    public ApiResponseDTO updateItemStatus(Long itemId, String newStatus, Long taskId) {
        return commandService.updateTaskItemStatus(itemId, newStatus, taskId);
    }
}
