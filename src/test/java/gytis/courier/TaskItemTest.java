/*
package com.example.courier;

import person.domain.gytis.courier.Admin;
import task.domain.gytis.courier.DeliveryStatus;
import order.domain.gytis.courier.ParcelStatus;
import com.example.courier.domain.*;
import person.domain.gytis.courier.Courier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class TaskItemTest {

    @Test
    void changeStatus_AllowsWhenNonFinalState() {
        TaskItem item = new TaskItemBuilder().withStatus(ParcelStatus.PICKED_UP).build();

        assertEquals(ParcelStatus.PICKED_UP, item.getStatus());
    }

    @Test
    void changeStatus_ThrowsWhenFinalState() {
        TaskItem item = new TaskItemBuilder().withStatus(ParcelStatus.DELIVERED).build();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> item.changeStatus(ParcelStatus.PICKED_UP, 123L)
        );

        assertEquals("Task item cannot be updated anymore.", ex.getMessage());
    }

    @Test
    void changeStatus_AddsNote_WhenStatusChanges() {
        TaskItem item = new TaskItemBuilder().withStatus(ParcelStatus.PICKED_UP).build();

        item.changeStatus(ParcelStatus.PICKED_UP, 123L);

        assertEquals(1, item.getNotes().size());
        assertTrue(item.getNotes().getFirst().contains("Picked up by Courier: 123"));
    }

    @Test
    void changeStatus_ThrowsWhenNullStatus() {
        TaskItem item = new TaskItemBuilder().withStatus(ParcelStatus.PICKING_UP).build();

        assertThrows(NullPointerException.class,
                () -> item.changeStatus(null, 123L));
    }

    @ParameterizedTest
    @EnumSource(ParcelStatus.class)
    void addDefaultStatusChangeNote_GeneratesCorrectNoteForAllStatuses(ParcelStatus status) {
        TaskItem item = new TaskItemBuilder().build();
        item.addDefaultStatusChangeNote(123L, status);

        assertFalse(item.getNotes().getFirst().isEmpty());
    }

    @Test
    void completeTask_WhenAllItemsFinal_UpdatesToComplete() {
        Task task = Task.create("pickup", mock(Courier.class), mock(Admin.class));
        TaskItem deliveredItem = new TaskItemBuilder().withStatus(ParcelStatus.DELIVERED).withTask(task).build();

        TaskItem failedItem = new TaskItemBuilder().withStatus(ParcelStatus.FAILED_DELIVERY).build();
        task.addTaskItems(List.of(deliveredItem, failedItem));

        Parcel parcelForDelivered = new Parcel();
        Parcel parcelForFailed = new Parcel();
        parcelForDelivered.setStatus(deliveredItem.getStatus());
        parcelForFailed.setStatus(failedItem.getStatus());

        deliveredItem = new TaskItemBuilder().withTask(task).withParcel(parcelForDelivered).withStatus(ParcelStatus.DELIVERED).build();
        failedItem = new TaskItemBuilder().withTask(task).withParcel(parcelForFailed).withStatus(ParcelStatus.FAILED_DELIVERY).build();

        task.completeTask();

        assertEquals(DeliveryStatus.COMPLETED, task.getDeliveryStatus());
        assertEquals(ParcelStatus.DELIVERED, deliveredItem.getParcel().getStatus());
        assertEquals(ParcelStatus.FAILED_DELIVERY, failedItem.getParcel().getStatus());
    }

    @Test
    void createNewDeliveryTask_HappyPath_CreatesTaskWithProperStatuses() {
        Parcel parcel = new Parcel();
        parcel.setStatus(ParcelStatus.PICKED_UP);


    }

}


class TaskItemBuilder {
    private Parcel parcel = new Parcel();
    private Order order = new Order();
    private Task task = mock(Task.class);

    TaskItemBuilder withStatus(ParcelStatus status) {
        parcel.setStatus(status);
        return this;
    }

    TaskItemBuilder withTask(Task task) {
        this.task = task;
        return this;
    }

    TaskItemBuilder withParcel(Parcel parcel) {
        this.parcel = parcel;
        return this;
    }

    TaskItem build() {
        return TaskItem.create(parcel, order, task);
    }
}

*/
