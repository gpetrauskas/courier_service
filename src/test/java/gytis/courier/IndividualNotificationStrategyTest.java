package gytis.courier;

import gytis.courier.adapter.out.strategy.notification.IndividualNotificationStrategy;
import gytis.courier.application.port.out.personnotification.PersonNotificationCommandPort;
import gytis.courier.domain.notification.Notification;
import gytis.courier.domain.notification.NotificationTarget;
import gytis.courier.domain.notification.NotificationTargetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class IndividualNotificationStrategyTest {
    private Notification notification;

    @Mock
    private PersonNotificationCommandPort personNotificationCommandPort;

    @InjectMocks
    private IndividualNotificationStrategy strategy;

    @BeforeEach
    void setUp() {
        notification = new Notification("title", "message", new NotificationTarget.Individual(1L));
    }

    @Test
    void successIndividualNotificationDelivery() {
        strategy.deliver(notification);

        verify(personNotificationCommandPort).deliverToRecipients(notification.getId(), List.of(1L));
    }

    @Test
    void throwOnWrongTargetType() {
        Notification notification1 = new Notification("title", "message", new NotificationTarget.Broadcast(NotificationTargetType.USER));

        assertThrows(IllegalArgumentException.class, () -> strategy.deliver(notification1));
    }
}
