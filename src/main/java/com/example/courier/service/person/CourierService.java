package com.example.courier.service.person;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.Courier;
import com.example.courier.domain.Task;
import com.example.courier.domain.TaskItem;
import com.example.courier.dto.response.task.CourierTaskDTO;
import com.example.courier.dto.mapper.DeliveryTaskMapper;
import com.example.courier.dto.request.UpdateTaskItemNotesRequest;
import com.example.courier.dto.request.UpdateTaskItemStatusRequest;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.CourierRepository;
import com.example.courier.repository.TaskItemRepository;
import com.example.courier.repository.TaskRepository;
import com.example.courier.service.NotificationService;
import com.example.courier.service.authorization.AuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class CourierService {

    private static final Logger logger = LoggerFactory.getLogger(CourierService.class);

    private final TaskRepository taskRepository;
    private final TaskItemRepository taskItemRepository;
    private final AuthorizationService authorizationService;
    private final CourierRepository courierRepository;
    private final NotificationService notificationService;
    private final DeliveryTaskMapper deliveryTaskMapper;

    public CourierService(TaskRepository taskRepository, TaskItemRepository taskItemRepository,
                          AuthorizationService authorizationService, CourierRepository courierRepository,
                          NotificationService notificationService, DeliveryTaskMapper deliveryTaskMapper) {
        this.taskRepository = taskRepository;
        this.taskItemRepository = taskItemRepository;
        this.authorizationService = authorizationService;
        this.courierRepository = courierRepository;
        this.notificationService = notificationService;
        this.deliveryTaskMapper = deliveryTaskMapper;
    }

    private void checkIfTaskCanBeUpdated(Task task) {
        if (!DeliveryStatus.isTaskItemUpdatable(task.getDeliveryStatus())) {
            throw new IllegalStateException("Cannot update notes for completed or canceled task");
        }
    }

    @Transactional
    public void processCourierCheckIn(Long taskId) {
        Task task = validateAndFetchTask(taskId);

        if (!DeliveryStatus.isValidToCheckIn(task.getDeliveryStatus())) {
            throw new IllegalStateException("Cannot check in.");
        }

        task.setDeliveryStatus(DeliveryStatus.AT_CHECKPOINT);
        task.setCompletedAt(LocalDateTime.now());
        taskRepository.save(task);

        Courier courier = task.getCourier();
        courier.setHasActiveTask(false);
        courierRepository.save(courier);

        notificationService.notifyAdmin(taskId, courier.getId());


        logger.info("Courier checked in: Task ID = {}, Courier ID = {}", taskId, courier.getId());
    }

    private Task validateAndFetchTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("No task found with such id"));

        checkIfTaskCanBeUpdated(task);
        authorizationService.validateCourierTaskAssignment(task);

        return task;
    }
}
