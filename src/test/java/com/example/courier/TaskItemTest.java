package com.example.courier;

import com.example.courier.common.DeliveryStatus;
import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.Parcel;
import com.example.courier.domain.Task;
import com.example.courier.domain.TaskItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskItemTest {

    @Test
    void changeStatus_AllowsWhenNonFinalState() {
        TaskItem item = new TaskItem();
        item.setStatus(ParcelStatus.PICKING_UP);
        item.changeStatus(ParcelStatus.PICKED_UP, 123L);
        assertEquals(ParcelStatus.PICKED_UP, item.getStatus());
    }

    @Test
    void changeStatus_ThrowsWhenFinalState() {
        TaskItem item = new TaskItem();
        item.setStatus(ParcelStatus.DELIVERED);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> item.changeStatus(ParcelStatus.PICKED_UP, 123L)
        );

        assertEquals("Task item cannot be updated anymore.", ex.getMessage());
    }

    @Test
    void changeStatus_AddsNote_WhenStatusChanges() {
        TaskItem item = new TaskItem();
        item.setStatus(ParcelStatus.PICKING_UP);

        item.changeStatus(ParcelStatus.PICKED_UP, 123L);

        assertEquals(1, item.getNotes().size());
        assertTrue(item.getNotes().getFirst().contains("Picked up by Courier: 123"));
    }

    @Test
    void changeStatus_ThrowsWhenNullStatus() {
        TaskItem item = new TaskItem();
        item.setStatus(ParcelStatus.PICKING_UP);

        assertThrows(NullPointerException.class,
                () -> item.changeStatus(null, 123L));
    }

    @ParameterizedTest
    @EnumSource(ParcelStatus.class)
    void addDefaultStatusChangeNote_GeneratesCorrectNoteForAllStatuses(ParcelStatus status) {
        TaskItem item = new TaskItem();
        item.addDefaultStatusChangeNote(123L, status);

        assertFalse(item.getNotes().getFirst().isEmpty());
    }

    @Test
    void completeTask_WhenAllItemsFinal_UpdatesToComplete() {
        Task task = new Task();
        TaskItem deliveredItem = new TaskItem();
        deliveredItem.setStatus(ParcelStatus.DELIVERED);

        TaskItem failedItem = new TaskItem();
        failedItem.setStatus(ParcelStatus.FAILED_DELIVERY);
        task.setItems(List.of(deliveredItem, failedItem));

        Parcel parcelForDelivered = new Parcel();
        Parcel parcelForFailed = new Parcel();
        parcelForDelivered.setStatus(deliveredItem.getStatus());
        parcelForFailed.setStatus(failedItem.getStatus());

        deliveredItem.setParcel(parcelForDelivered);
        failedItem.setParcel(parcelForFailed);

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
