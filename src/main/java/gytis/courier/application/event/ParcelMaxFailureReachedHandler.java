package gytis.courier.application.event;

import gytis.courier.application.command.CreateNotificationCommand;
import gytis.courier.application.port.in.notification.NotificationCommandUseCase;
import gytis.courier.domain.notification.NotificationTargetType;
import gytis.courier.domain.event.ParcelMaxFailuresReachedEvent;
import gytis.courier.domain.notification.NotificationTarget;
import org.springframework.context.event.EventListener;
import org.springframework.jmx.export.notification.UnableToSendNotificationException;
import org.springframework.stereotype.Component;

@Component
public class ParcelMaxFailureReachedHandler {
    private final NotificationCommandUseCase useCase;

    public ParcelMaxFailureReachedHandler(NotificationCommandUseCase useCase) {
        this.useCase = useCase;
    }

    @EventListener
    public void handle(ParcelMaxFailuresReachedEvent event) {

        System.out.println("in parcel max failure reached even handler... at the method start... before the try catch...");
        try {
            useCase.create(
                    new CreateNotificationCommand(
                            "Parcel reached failures limit",
                            "Parcel ID " + event.parcelId() + "was failed to deliver/pickup " + event.failuresCount() + " times." +
                                    "Extra action need to be taken.",
                            new NotificationTarget.Broadcast(NotificationTargetType.ADMIN)
                    )
            );
        } catch (UnableToSendNotificationException ex) {
            throw new UnableToSendNotificationException("Notification failed to be sent. ", ex);
        }
    }
}
