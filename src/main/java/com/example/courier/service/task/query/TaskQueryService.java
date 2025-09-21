package com.example.courier.service.task.query;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.domain.Task;
import com.example.courier.dto.PaginatedResponseDTO;
import com.example.courier.dto.mapper.DeliveryTaskMapper;
import com.example.courier.dto.request.task.DeliveryTaskFilterDTO;
import com.example.courier.dto.response.task.AdminTaskDTO;
import com.example.courier.dto.response.task.CourierTaskDTO;
import com.example.courier.dto.response.task.TaskBase;
import com.example.courier.repository.TaskRepository;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.specification.task.TaskSpecificationBuilder;
import com.example.courier.util.PageableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Query side service for retrieving {@link Task} data.
 *
 * <p>
 *     Provides read operations for both admins and couriers.
 *     Uses {@link TaskSpecificationBuilder} for dynamic filtering and
 *     {@link DeliveryTaskMapper} for mapping entities to DTOs.
 * </p>
 */
@Service
public class TaskQueryService {
    private final TaskRepository taskRepository;
    private final CurrentPersonService currentPersonService;
    private final TaskSpecificationBuilder specificationBuilder;
    private final DeliveryTaskMapper mapper;

    public TaskQueryService(TaskRepository taskRepository, CurrentPersonService currentPersonService,
                            TaskSpecificationBuilder specificationBuilder, DeliveryTaskMapper mapper) {
        this.taskRepository = taskRepository;
        this.currentPersonService = currentPersonService;
        this.specificationBuilder = specificationBuilder;
        this.mapper = mapper;
    }

    /**
     * Returns a paginated list of tasks for the current user.
     * <ul>
     *     <li>Admins receive {@link AdminTaskDTO} results.</li>
     *     <li>Couriers receive {@link CourierTaskDTO} results.</li>
     * </ul>
     *
     * @param filterDTO filter and pagination parameters
     * @return paginated list of tasks
     * */
    public PaginatedResponseDTO<? extends TaskBase> getAllTasksList(DeliveryTaskFilterDTO filterDTO) {
        Pageable pageable = PageableUtils.createPageable(filterDTO.page(), filterDTO.size(), filterDTO.sortBy(), filterDTO.direction().toString());

        return currentPersonService.isAdmin()
                ? getAdminTasks(filterDTO, pageable)
                : getCourierTaskHistory(pageable);
    }

    /**
     * Returns the current active tasks for the authenticated courier.
     *
     * @return list of courier tasks
     */
    public List<CourierTaskDTO> getCurrentCourierTask() {
        Long id = currentPersonService.getCurrentPersonId();
        Set<DeliveryStatus> statuses = DeliveryStatus.currentStatuses();

        List<Task> tasks = taskRepository.findByCourierIdAndDeliveryStatusIn(id, statuses);

        return tasks.stream()
                .map(task -> mapper.toCourierTaskDTO(task, task.getTaskType()))
                .toList();
    }

    /* Helper methods
     */

    /**
     * Returns paginated list of tasks for admins, applying filters and eager loading details.
     */
    private PaginatedResponseDTO<AdminTaskDTO> getAdminTasks(DeliveryTaskFilterDTO dto, Pageable pageable) {
        Specification<Task> specification = specificationBuilder.buildTaskSpecification(dto);

        Page<Task> page = taskRepository.findAll(specification, pageable);
        List<Long> taskIds = page.getContent().stream().map(Task::getId).toList();

        List<Task> tasks = taskRepository.findAllWithDetailsByIdIn(taskIds);

        return PageableUtils.toPaginatedResponse(tasks.stream().map(mapper::toDeliveryTaskDTO).toList(), page);
    }

    /**
     * Returns paginated history of tasks for the current courier with eager loading details.
     */
    private PaginatedResponseDTO<CourierTaskDTO> getCourierTaskHistory(Pageable pageable) {
        Long currentCourierId = currentPersonService.getCurrentPersonId();

        Page<Task> tasks = taskRepository.findByCourierIdAndDeliveryStatusIn(currentCourierId, DeliveryStatus.historicalStatuses(), pageable);

        return PageableUtils.mapPage(tasks, task -> mapper.toCourierTaskDTO(task, task.getTaskType()));
    }
}
