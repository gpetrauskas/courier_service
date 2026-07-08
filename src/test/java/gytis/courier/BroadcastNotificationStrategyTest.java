package gytis.courier;

import gytis.courier.adapter.out.strategy.notification.BroadcastNotificationStrategy;
import gytis.courier.application.port.out.auth.PersonQueryPort;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BroadcastNotificationStrategyTest {
    private Notification notification;

    @Mock
    private PersonQueryPort personQueryPort;
    @Mock
    private PersonNotificationCommandPort notificationCommandPort;

    @InjectMocks
    private BroadcastNotificationStrategy broadcastNotificationStrategy;

    @BeforeEach
    void setUp() {
        notification = new Notification("title", "message", new NotificationTarget.Broadcast(NotificationTargetType.USER));
        notification.withId(99L);
    }

    @Test
    void successOnDeliveringNoMoreThanNotifications() {
        List<Long> ids = simulatePersonIds(500);

        when(personQueryPort.getAllActiveUserIds(0, 1000)).thenReturn(ids);

        broadcastNotificationStrategy.deliver(notification);

        verify(notificationCommandPort, times(1)).deliverToRecipients(notification.getId(), ids);
    }

    @Test
    void successOnDeliveringMoreThan1000() {
        List<Long> first1000 = simulatePersonIds(1000);
        List<Long> second1000 = simulatePersonIds(1000);
        List<Long> last = simulatePersonIds(1);

        when(personQueryPort.getAllActiveUserIds(0, 1000)).thenReturn(first1000);
        when(personQueryPort.getAllActiveUserIds(1, 1000)).thenReturn(second1000);
        when(personQueryPort.getAllActiveUserIds(2, 1000)).thenReturn(last);

        broadcastNotificationStrategy.deliver(notification);

        verify(notificationCommandPort, times(3)).deliverToRecipients(anyLong(), anyList());
    }

    private List<Long> simulatePersonIds(int idsCount) {
        List<Long> list = new ArrayList<>();

        for (int i = 1; i <= idsCount; i++) {
            list.add((long) i);
        }

        return list;
    }
}
