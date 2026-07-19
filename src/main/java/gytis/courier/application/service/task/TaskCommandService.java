package gytis.courier.application.service.task;

import gytis.courier.application.command.AddItemNoteCommand;
import gytis.courier.application.command.CreateTaskCommand;
import gytis.courier.application.port.in.task.AdminTaskCommandUseCase;
import gytis.courier.application.port.in.task.CourierTaskCommandUseCase;
import gytis.courier.application.port.in.task.ParcelAssignmentFacade;
import gytis.courier.application.port.out.DomainEventPublisher;
import gytis.courier.application.port.out.order.OrderQueryPort;
import gytis.courier.application.port.out.task.TaskCommandPort;
import gytis.courier.application.service.activitylog.ActivityLogService;
import gytis.courier.application.service.person.CourierCommandService;
import gytis.courier.domain.task.*;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskCommandService implements AdminTaskCommandUseCase, CourierTaskCommandUseCase {
    private final TaskCommandPort commandPort;
    private final OrderQueryPort orderQueryPort;
    private final ParcelAssignmentFacade parcelAssignmentFacade;
    private final DomainEventPublisher publisher;
    private final TaskAssignmentPolicy assignmentPolicy;
    private final CourierCommandService courierService;
    private final ActivityLogService logService;

    public TaskCommandService(TaskCommandPort commandPort, OrderQueryPort orderQueryPort,
                              DomainEventPublisher publisher, TaskAssignmentPolicy assignmentPolicy,
                              ParcelAssignmentFacade parcelAssignmentFacade, CourierCommandService courierService, ActivityLogService logService) {
        this.commandPort = commandPort;
        this.orderQueryPort = orderQueryPort;
        this.parcelAssignmentFacade = parcelAssignmentFacade;
        this.publisher = publisher;
        this.assignmentPolicy = assignmentPolicy;
        this.courierService = courierService;
        this.logService = logService;
    }

    @Override
    @Transactional
    public void createTask(CreateTaskCommand command) {
        assignmentPolicy.ensureCourierIsAvailable(command.courierId());

        List<TaskItemCreationSnapshot> taskItemCreationSnapshots = orderQueryPort.findOrdersByParcelIds(command.parcelIds());
        ParcelAssignmentValidator.validate(command.parcelIds(), taskItemCreationSnapshots, command.taskType());

        Task task = Task.create(taskItemCreationSnapshots, command.courierId(), command.adminId(), command.taskType());

        parcelAssignmentFacade.assignParcels(
                task.getTaskItems().stream()
                .map(TaskItem::getParcelId)
                .toList()
        );

        if (command.taskType() == TaskType.DELIVERY) {
            parcelAssignmentFacade.markParcelsDelivering(task.getTaskItems().stream()
                    .map(TaskItem::getParcelId)
                    .toList());
        }

        Task taskWithId = commandPort.create(task);
        publisher.publish(task.pullEvents());

        logService.saveLog("ADMIN", "task created", "Task #" + taskWithId.getId() + " with " + task.getTaskItems().size() + " items, created and assigned to courier #" + taskWithId.getCourierId());
    }

    @Override
    @Transactional
    public void addItems(Long taskId, List<Long> parcelIds) {
        Task task = findWithItemsById(taskId);

        List<TaskItemCreationSnapshot> snapshots = orderQueryPort.findOrdersByParcelIds(parcelIds);
        task.addItems(snapshots);

        parcelAssignmentFacade.assignParcels(parcelIds);

        commandPort.updateWithItems(task);
    }

    @Override
    @Transactional
    public void cancel(Long taskId, Long adminId) {
        Task task = findWithItemsById(taskId);

        parcelAssignmentFacade.unassignParcels(task.cancel(adminId));

        commandPort.updateWithItems(task);
        publisher.publish(task.pullEvents());

        logService.saveLog("ADMIN", "cancel", "Task #" + task.getId() + " canceled");
    }

    @Override
    @Transactional
    public void updateItemStatus(UpdateItemStatusCommand command) {
        Task task = findWithItemsById(command.taskId());

        task.validateCourierOwnership(command.myId());
        task.updateItemStatus(command.taskItemId(), command.status());

        commandPort.updateWithItems(task);
        publisher.publish(task.pullEvents());

        logService.saveLog("COURIER", "update item", "Task Item #" + command.taskItemId() + " status in Task #" + task.getId() + " was updated to " + command.status());
    }

    @Override
    @Transactional
    public void addItemNote(AddItemNoteCommand command) {
        Task task = findWithItemsById(command.taskId());

        task.validateCourierOwnership(command.myId());
        task.addTaskItemNote(command.itemId(), command.note());

        commandPort.updateWithItems(task);

        logService.saveLog("COURIER", "note add", "Courier #" + task.getCourierId() + " added note for item " + command.itemId() + " in Task #" + task.getId());
    }

    @Override
    @Transactional
    public void complete(Long taskId) {
        Task task = findWithItemsById(taskId);

        task.complete();

        commandPort.updateWithItems(task);
        publisher.publish(task.pullEvents());

        logService.saveLog("ADMIN", "task complete", "Task #" + task.getId() + " was completed");
    }


    @Override
    @Transactional
    public void removeItem(Long taskId, Long itemId, Long adminId) {
        Task task = findWithItemsById(taskId);

        Long parcelId = task.removeItem(itemId, adminId);
        parcelAssignmentFacade.unassignParcels(List.of(parcelId));

        commandPort.updateWithItems(task);
        publisher.publish(task.pullEvents());
    }

    @Override
    @Transactional
    public void changeCourier(Long taskId, Long courierId) {
        Task task = findBasicById(taskId);

        courierService.deactivate(task.getCourierId());
        courierService.activate(courierId);

        task.changeCourier(courierId).ifPresent(publisher::publish);

        commandPort.update(task);

        logService.saveLog("ADMIN", "courier change", "Task #" + task.getId() + " new courier assigned");
    }

    @Override
    @Transactional
    public void checkIn(Long taskId, Long myId) {
        Task task = findWithItemsById(taskId);

        task.validateCourierOwnership(myId);
        task.checkIn();

        commandPort.updateWithItems(task);
        publisher.publish(task.pullEvents());

        logService.saveLog("COURIER", "check-in", "Courier #" + task.getCourierId() + " checked in with Task #" + task.getId() + " size of " + task.getTaskItems().size());
    }

    private Task findWithItemsById(Long id) {
        return commandPort.getWithItemsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    private Task findBasicById(Long id) {
        return commandPort.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }
}
