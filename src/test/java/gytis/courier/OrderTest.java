package gytis.courier;

import gytis.courier.domain.address.AddressDetails;
import gytis.courier.domain.order.*;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderTest {
    private Order order;

    @BeforeEach
    void setUp() {
        OrderAddress orderAddress = OrderAddress.from(
                new AddressDetails("me", "elm street", "20", "1", "vilnius", "12345", "12345678"));
        Parcel parcel = Parcel.restore(
                50L, 49L, "light_weight", BigDecimal.valueOf(2), 48L, "small_size",
                BigDecimal.valueOf(3), 0, "books", "123", false, ParcelStatus.WAITING_FOR_PAYMENT);

        order = Order.restore(1L, 2L, orderAddress, orderAddress, parcel, 47L, "standard",
                "1-3 days delivery", BigDecimal.valueOf(3), OrderStatus.PENDING, LocalDateTime.now());
    }

    @Test
    void successOnCancel() {
        order.cancel();

        assertEquals(OrderStatus.CANCELED, order.getStatus());
        assertEquals(ParcelStatus.CANCELED, order.getParcel().getStatus());
    }

    @Test
    void throwsOnCancel() {
        order.updateStatus(OrderStatus.CONFIRMED);

        assertThrows(ValidationException.class, () -> order.cancel());
    }

    @Test
    void throwsOnManualStatusChangeToCompleted() {
        assertThrows(ValidationException.class, () -> order.updateStatus(OrderStatus.COMPLETED));
    }

    @Test
    void throwOnValidateUpdatableWhenStatusIsFinalState() {
        order.updateStatus(OrderStatus.CANCELED);

        assertThrows(ValidationException.class, () -> order.validateUpdatable());
    }

    @Test
    void successOnMarkConfirmed() {
        order.markConfirmed();

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        assertEquals(ParcelStatus.PICKING_UP, order.getParcel().getStatus());
    }
}
