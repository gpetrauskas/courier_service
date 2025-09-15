package com.example.courier.service.task.command;

import com.example.courier.domain.*;
import com.example.courier.dto.CreateTaskDTO;
import com.example.courier.repository.TaskRepository;
import com.example.courier.service.order.query.OrderQueryService;
import com.example.courier.service.person.query.PersonLookupService;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.service.task.TaskItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    public TaskCommandService(PersonLookupService personLookupService, TaskRepository repository,
                              OrderQueryService orderQueryService, TaskItemService taskItemService, CurrentPersonService currentPersonService) {
        this.personLookupService = personLookupService;
        this.repository = repository;
        this.orderQueryService = orderQueryService;
        this.taskItemService = taskItemService;
        this.currentPersonService = currentPersonService;
    }


    @Transactional
    public void initiateNewTask(CreateTaskDTO dto) {
        logger.info("Creating a task list for the courier: {}", dto.courierId());

        Courier courier = personLookupService.fetchPersonByIdAndType(dto.courierId(), Courier.class);
        courier.activateTask();
        Admin admin = currentPersonService.getCurrentPersonAs(Admin.class);

        Task task = Task.create(dto.taskType(), courier, admin);
        List<TaskItem> taskItems = prepareTaskItems(dto.parcelsIds(), task);

        task.addTaskItems(taskItems);
        repository.save(task);
    }

    /* Helper methods
    */

    private List<TaskItem> prepareTaskItems(List<Long> parcelsIds, Task task) {
        List<Order> orderListWithParcelDetails = orderQueryService.getAllOrdersWithParcelByParcelIds(parcelsIds);

        task.transitParcelsIfRequired(orderListWithParcelDetails);
        return taskItemService.createTaskItems(orderListWithParcelDetails, task);
    }
}
