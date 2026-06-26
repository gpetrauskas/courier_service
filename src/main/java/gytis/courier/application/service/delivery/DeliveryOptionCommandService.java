package gytis.courier.application.service.delivery;

import gytis.courier.application.port.in.delivery.AddDeliveryOptionUseCase;
import gytis.courier.application.port.in.delivery.DeleteDeliveryOptionUseCase;
import gytis.courier.application.port.in.delivery.UpdateDeliveryOptionUseCase;
import gytis.courier.application.port.out.delivery.DeliveryOptionCommandPort;
import gytis.courier.domain.delivery.DeliveryOption;
import org.springframework.stereotype.Service;

@Service
public class DeliveryOptionCommandService implements AddDeliveryOptionUseCase, UpdateDeliveryOptionUseCase, DeleteDeliveryOptionUseCase {
    private final DeliveryOptionCommandPort port;

    public DeliveryOptionCommandService(DeliveryOptionCommandPort port) {
        this.port = port;
    }

    @Override
    public void updateDeliveryMethod(UpdateDeliveryOptionCommand command) {
        DeliveryOption old = port.findById(command.id());
        DeliveryOption updated = old.update(command);

        if (old == updated) {
            throw new IllegalArgumentException("Nothing was updated");
        }

        port.save(updated);
    }

    @Override
    public void add(CreateDeliveryOptionCommand command) {
        DeliveryOption newOption = DeliveryOption.create(command);
        port.create(newOption);
    }

    @Override
    public void delete(Long id) {
        port.delete(id);
    }
}
