package gytis.courier;

import gytis.courier.domain.event.DomainEvent;
import gytis.courier.domain.event.TaskCompletedEvent;
import gytis.courier.domain.order.ParcelStatus;
import gytis.courier.domain.task.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {
    private Task task;

    @BeforeEach
    void setUp() {
        TaskItem item = TaskItem.restore()
                .id(9L)
                .parcelStatus(ParcelStatus.PICKING_UP)
                .parcelId(8L)
                .contents("books")
                .deliveryMethodName("standard")
                .recipientAddressId(20L)
                .senderAddressId(21L)
                .notes(Set.of())
                .build();

        List<TaskItem> items = new ArrayList<>();
        items.add(item);

        task = Task.restore()
                .id(1L)
                .courierId(2L)
                .taskType(TaskType.PICKUP)
                .deliveryStatus(DeliveryStatus.IN_PROGRESS)
                .createdByAdminId(3L)
                .canceledByAdminId(null)
                .completedAt(null)
                .createdAt(LocalDateTime.now())
                .items(items)
                .build();

    }

    @Test
    void successOnAddingItems() {
        TaskItemCreationSnapshot snapshot = new TaskItemCreationSnapshot(
                30L, ParcelStatus.PICKING_UP, 31L, 32L, "rocks", "overnight"
        );

        task.addItems(List.of(snapshot));

        assertEquals(2, task.getTaskItems().size());
    }

    @Test
    void throwOnAddItemWithSameIdAlreadyInList() {
        TaskItemCreationSnapshot snapshot = new TaskItemCreationSnapshot(
                9L, ParcelStatus.PICKING_UP, 31L, 32L, "rocks", "overnight"
        );

        assertThrows(IllegalStateException.class, () -> task.addItems(List.of(snapshot)));
    }

    @Test
    void successOnChangeCourier() {
        Long newCourierId = 99L;

        var event = task.changeCourier(newCourierId);

        assertEquals(newCourierId, task.getCourierId());
        assertTrue(event.isPresent());
    }

    @Test
    void successOnCancel() {
        List<Long> parcelIds = task.cancel(98L);

        assertEquals(DeliveryStatus.CANCELED, task.getDeliveryStatus());
        assertEquals(ParcelStatus.CANCELED, task.getTaskItems().getFirst().getParcelStatus());
        assertEquals(1, task.pullEvents().size());
        assertEquals(task.getTaskItems().getFirst().getParcelId(), parcelIds.getFirst());
    }

    @Test
    void successTaskStatusChangeOnItemStatusUpdateWhenAllFinalState() {
        task.updateItemStatus(task.getTaskItems().getFirst().getId(), ParcelStatus.PICKED_UP);

        assertEquals(DeliveryStatus.RETURNING_TO_STATION, task.getDeliveryStatus());
        assertEquals(ParcelStatus.PICKED_UP, task.getTaskItems().getFirst().getParcelStatus());
        assertEquals(1, task.pullEvents().size());
    }

    @Test
    void successOnComplete() {
        TaskItem item = TaskItem.restore().id(88L).parcelId(87L).parcelStatus(ParcelStatus.FAILED_PICKUP).build();
        TaskItem secondItem = TaskItem.restore().id(86L).parcelId(85L).parcelStatus(ParcelStatus.PICKED_UP).build();
        List<TaskItem> items = List.of(item, secondItem);

        Task task1 = Task.restore().id(1L).taskType(TaskType.PICKUP).deliveryStatus(DeliveryStatus.AT_CHECKPOINT).items(items).canceledByAdminId(2L).courierId(3L).build();

        task1.complete();
        List<DomainEvent> event = task1.pullEvents();
        TaskCompletedEvent completedEvent = (TaskCompletedEvent) event.getFirst();

        assertEquals(DeliveryStatus.COMPLETED, task1.getDeliveryStatus());
        assertNotNull(task1.getCompletedAt());
        assertFalse(completedEvent.success().isEmpty());
        assertFalse(completedEvent.failed().isEmpty());
    }

    @Test
    void throwOnCompleteCannotTransit() {
        task.cancel(1L);

        assertThrows(IllegalStateException.class, () -> task.complete());
    }

    @Test
    void successOnCourierValidation() {
        task.validateCourierOwnership(task.getCourierId());
    }

    @Test
    void throwsOnCourierOwnershipValidation() {
        Long wrongCourierId = 100L;

        assertThrows(IllegalStateException.class, () -> task.validateCourierOwnership(wrongCourierId));
    }

    @Test
    void successOnCancelIfNoItemsExistOnItemRemove() {
        task.removeItem(task.getTaskItems().getFirst().getId(), task.getCreatedByAdminId());

        assertEquals(DeliveryStatus.CANCELED, task.getDeliveryStatus());
    }

    @Test
    void throwsOnRemoveItemIsInFinalState() {
        task.getTaskItems().getFirst().updateStatus(ParcelStatus.PICKED_UP);

        assertThrows(IllegalStateException.class, () -> task.removeItem(task.getTaskItems().getFirst().getId(), task.getCreatedByAdminId()));
    }

}
