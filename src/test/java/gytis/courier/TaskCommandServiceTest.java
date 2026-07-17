package gytis.courier;

import gytis.courier.application.command.AddItemNoteCommand;
import gytis.courier.application.command.CreateTaskCommand;
import gytis.courier.application.port.in.task.ParcelAssignmentFacade;
import gytis.courier.application.port.out.DomainEventPublisher;
import gytis.courier.application.port.out.order.OrderQueryPort;
import gytis.courier.application.port.out.task.TaskCommandPort;
import gytis.courier.application.service.person.CourierCommandService;
import gytis.courier.application.service.task.TaskCommandService;
import gytis.courier.application.service.task.UpdateItemStatusCommand;
import gytis.courier.domain.event.CourierChangeEvent;
import gytis.courier.domain.order.ParcelStatus;
import gytis.courier.domain.task.*;

import gytis.courier.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskCommandServiceTest {
    private final Long taskId = 3L;
    private final Long adminId = 5L;
    private final Long courierId = 6L;
    private final Long parcelId = 8L;
    private final Long senderAddressId = 9L;
    private final Long recipientAddressId = 10L;
    private final List<Long> parcelsIds = List.of(8L);
    private final String parcelContents = "books";
    private final String deliveryMethodName = "overnight";

    private final Task task = Task.create(List.of(), courierId, adminId, TaskType.PICKUP);
    private final List<TaskItemCreationSnapshot> snapshots = List.of(new TaskItemCreationSnapshot(
            parcelId, ParcelStatus.PICKING_UP, senderAddressId, recipientAddressId, parcelContents, deliveryMethodName
    ));

    @Mock private TaskCommandPort taskCommandPort;
    @Mock private OrderQueryPort orderQueryPort;
    @Mock private ParcelAssignmentFacade parcelAssignmentFacade;
    @Mock private DomainEventPublisher eventPublisher;
    @Mock private TaskAssignmentPolicy taskAssignmentPolicy;
    @Mock private CourierCommandService courierCommandService;

    @InjectMocks private TaskCommandService service;

    @Test
    void successOnCreateTask() {
        CreateTaskCommand createTaskCommand = new CreateTaskCommand(adminId, courierId, TaskType.PICKUP, parcelsIds);

        when(orderQueryPort.findOrdersByParcelIds(createTaskCommand.parcelIds())).thenReturn(snapshots);

        service.createTask(createTaskCommand);

        verify(taskCommandPort).create(any(Task.class));
        verify(eventPublisher).publish(anyList());
    }

    @Test
    void throwsWhenCourierNtAvailable() {
        doThrow(IllegalStateException.class).when(taskAssignmentPolicy).ensureCourierIsAvailable(courierId);
        CreateTaskCommand createTaskCommand = new CreateTaskCommand(adminId, courierId, TaskType.PICKUP, parcelsIds);

        assertThrows(IllegalStateException.class, () -> service.createTask(createTaskCommand));

        verify(eventPublisher, never()).publish(anyList());
        verifyNoInteractions(taskCommandPort);
    }

    @Test
    void successOnAddItems() {
        when(taskCommandPort.getWithItemsById(taskId)).thenReturn(Optional.of(task));
        when(orderQueryPort.findOrdersByParcelIds(parcelsIds)).thenReturn(snapshots);

        service.addItems(taskId, parcelsIds);

        verify(parcelAssignmentFacade).assignParcels(parcelsIds);
        verify(taskCommandPort).updateWithItems(task);
    }

    @Test
    void throwsOnAddItemsTaskNotFound() {
        when(taskCommandPort.getWithItemsById(taskId)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> service.addItems(taskId, parcelsIds));

        verify(taskCommandPort, never()).updateWithItems(any());
        verify(parcelAssignmentFacade, never()).assignParcels(anyList());
    }

    @Test
    void throwOnAddItemsOrdersNotFound() {
        when(taskCommandPort.getWithItemsById(taskId)).thenReturn(Optional.of(task));
        when(orderQueryPort.findOrdersByParcelIds(parcelsIds)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> service.addItems(taskId, parcelsIds));

        verify(taskCommandPort, never()).update(task);
        verify(parcelAssignmentFacade, never()).assignParcels(anyList());
    }

    @Test
    void successOnChangeCourier() {
        when(taskCommandPort.getById(taskId)).thenReturn(Optional.of(task));

        Long newCourierId = 7L;
        service.changeCourier(taskId, newCourierId);

        verify(courierCommandService).deactivate(courierId);
        verify(courierCommandService).activate(newCourierId);
        verify(eventPublisher).publish(any(CourierChangeEvent.class));
        verify(taskCommandPort).update(task);
    }

    @Test
    void successOnTaskCancel() {
        when(taskCommandPort.getWithItemsById(taskId)).thenReturn(Optional.of(task));

        service.cancel(taskId, adminId);

        verify(taskCommandPort).updateWithItems(task);
        verify(eventPublisher).publish(anyList());
    }

    @Test
    void successOnUpdateItemStatus() {
        TaskItem taskItem = TaskItem.restore().id(55L).parcelStatus(ParcelStatus.PICKING_UP).build();
        Task task1 = Task.restore().id(1L).items(List.of(taskItem)).courierId(2L).deliveryStatus(DeliveryStatus.IN_PROGRESS).build();
        when(taskCommandPort.getWithItemsById(task1.getId())).thenReturn(Optional.of(task1));

        service.updateItemStatus(new UpdateItemStatusCommand(task1.getCourierId(), task1.getId(), taskItem.getId(), ParcelStatus.PICKED_UP));

        verify(taskCommandPort).updateWithItems(task1);
    }

    @Test
    void successOnAddItemNote() {
        TaskItem taskItem = TaskItem.restore().id(2L).parcelStatus(ParcelStatus.PICKING_UP).build();
        Task task1 = Task.restore().id(1L).items(List.of(taskItem)).courierId(3L).deliveryStatus(DeliveryStatus.IN_PROGRESS).build();
        when(taskCommandPort.getWithItemsById(task1.getId())).thenReturn(Optional.of(task1));

        service.addItemNote(new AddItemNoteCommand(task1.getCourierId(), task1.getId(), taskItem.getId(), "note here"));

        verify(taskCommandPort).updateWithItems(task1);
    }

    @Test
    void successOnTaskComplete() {
        TaskItem item = TaskItem.restore().id(1L).parcelStatus(ParcelStatus.PICKED_UP).build();
        Task task1 = Task.restore().id(2L).deliveryStatus(DeliveryStatus.AT_CHECKPOINT).courierId(3L).items(List.of(item)).build();

        when(taskCommandPort.getWithItemsById(task1.getId())).thenReturn(Optional.of(task1));

        service.complete(task1.getId());

        verify(taskCommandPort).updateWithItems(task1);
        verify(eventPublisher).publish(anyList());
    }

    @Test
    void successOnItemRemove() {
        TaskItem taskItem = TaskItem.restore().id(99L).parcelStatus(ParcelStatus.PICKING_UP).parcelId(parcelId).build();
        Task task1 = Task.restore().id(taskId).courierId(courierId).createdByAdminId(adminId).items(List.of(taskItem)).deliveryStatus(DeliveryStatus.IN_PROGRESS).build();

        when(taskCommandPort.getWithItemsById(taskId)).thenReturn(Optional.of(task1));

        service.removeItem(taskId, 99L, adminId);

        verify(parcelAssignmentFacade).unassignParcels(List.of(taskItem.getParcelId()));
        verify(taskCommandPort).updateWithItems(task1);
    }

    @Test
    void successOnCheckIn() {
        TaskItem item = TaskItem.restore().id(99L).parcelId(parcelId).parcelStatus(ParcelStatus.PICKED_UP).build();
        Task task1 = Task.restore().id(taskId).courierId(courierId).items(List.of(item)).deliveryStatus(DeliveryStatus.RETURNING_TO_STATION).build();

        when(taskCommandPort.getWithItemsById(task1.getId())).thenReturn(Optional.of(task1));

        service.checkIn(taskId, courierId);

        verify(taskCommandPort).updateWithItems(task1);
        verify(eventPublisher).publish(anyList());
    }
}
