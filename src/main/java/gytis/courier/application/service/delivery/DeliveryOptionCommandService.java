package gytis.courier.application.service.delivery;

import gytis.courier.application.port.in.activityLog.ActivityLogUseCase;
import gytis.courier.application.port.in.delivery.AddDeliveryOptionUseCase;
import gytis.courier.application.port.in.delivery.DeleteDeliveryOptionUseCase;
import gytis.courier.application.port.in.delivery.UpdateDeliveryOptionUseCase;
import gytis.courier.application.port.out.delivery.DeliveryOptionCommandPort;
import gytis.courier.domain.delivery.DeliveryOption;
import org.springframework.stereotype.Service;

@Service
public class DeliveryOptionCommandService implements AddDeliveryOptionUseCase, UpdateDeliveryOptionUseCase, DeleteDeliveryOptionUseCase {
    private final DeliveryOptionCommandPort port;
    private final ActivityLogUseCase logUseCase;

    public DeliveryOptionCommandService(DeliveryOptionCommandPort port, ActivityLogUseCase logUseCase) {
        this.port = port;
        this.logUseCase = logUseCase;
    }

    @Override
    public void updateDeliveryMethod(UpdateDeliveryOptionCommand command) {
        DeliveryOption old = port.findById(command.id());
        DeliveryOption updated = old.update(command);

        if (old == updated) {
            throw new IllegalArgumentException("Nothing was updated");
        }

        port.save(updated);

        logUseCase.saveLog("ADMIN", "option update", "#" + updated.id() + " option updated");
    }

    @Override
    public void add(CreateDeliveryOptionCommand command) {
        DeliveryOption newOption = DeliveryOption.create(command);
        port.create(newOption);

        logUseCase.saveLog("ADMIN", "option added", newOption.name() + " option added");
    }

    @Override
    public void delete(Long id) {
        port.delete(id);
        logUseCase.saveLog("ADMIN", "option delete", "#" + id + " option deleted");
    }
}
