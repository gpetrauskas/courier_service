package gytis.courier;

import gytis.courier.domain.order.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParcelTest {
    private Parcel parcel;

    @BeforeEach
    void setUp() {
        parcel = Parcel.restore(
                50L, 49L, "light_weight", BigDecimal.valueOf(2), 48L, "small_size",
                BigDecimal.valueOf(3), 0, "books", "123", false, ParcelStatus.WAITING_FOR_PAYMENT);
    }

    @Test
    void successMarkAsPickingUp() {
        parcel.markAsPickingUp();

        assertEquals(ParcelStatus.PICKING_UP, parcel.getStatus());
    }

    @Test
    void throwOnMarkAsPickingUpWhenStatusIsNotValid() {
        parcel.markAsPickingUp();

        assertThrows(IllegalStateException.class, () -> parcel.markAsPickingUp());
    }

    @Test
    void successTransitionToDelivery() {
        parcel.markAsPickingUp();
        parcel.changeStatus(ParcelStatus.PICKED_UP);
        parcel.transitionToDelivery();

        assertEquals(ParcelStatus.DELIVERING, parcel.getStatus());
    }

    @Test
    void successOnFailedDeliveryAttemptAddEvent() {
        parcel.failedDeliveryAttemptAdd();
        parcel.failedDeliveryAttemptAdd();

        parcel.failedDeliveryAttemptAdd();

        assertEquals(1, parcel.getEventSize());
        assertEquals(3, parcel.getFailuresCount());
    }

    @Test
    void successOnParcelSectionUpdate() {
        parcel.markAsPickingUp();
        ParcelSectionUpdateCommand command = new ParcelSectionUpdateCommand(ParcelStatus.PICKED_UP, "something");

        parcel.updateSection(command);

        assertEquals(command.status(), parcel.getStatus());
        assertEquals(command.contents(), parcel.getContents());
    }

    @Test
    void throwsOnParcelUpdateStatusChangeWhenTransitionNotAllowed() {
        ParcelSectionUpdateCommand command = new ParcelSectionUpdateCommand(ParcelStatus.PICKED_UP, null);

        assertThrows(IllegalArgumentException.class, () -> parcel.updateSection(command));
    }

    @Test
    void throwsOnIsAddressEditableWhenParcelInFinalState() {
        parcel.markAsPickingUp();
        parcel.changeStatus(ParcelStatus.PICKED_UP);

        assertThrows(IllegalStateException.class, () -> parcel.isAddressEditable());
    }

    @Test
    void successOnFullParcelLifeCycleFlow() {
        parcel.markAsPickingUp();
        parcel.changeStatus(ParcelStatus.PICKED_UP);
        parcel.transitionToDelivery();
        parcel.changeStatus(ParcelStatus.DELIVERED);

        assertEquals(ParcelStatus.DELIVERED, parcel.getStatus());
    }

}
