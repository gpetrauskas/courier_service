package gytis.courier.application.service.delivery;

import gytis.courier.application.port.in.delivery.DeliveryOptionQueryUseCase;
import gytis.courier.application.port.out.delivery.DeliveryOptionQueryPort;
import gytis.courier.application.readmodel.deliveryoption.DeliveryOptionReadModel;
import gytis.courier.domain.delivery.DeliveryGroup;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DeliveryOptionQueryService implements DeliveryOptionQueryUseCase {
    private final DeliveryOptionQueryPort port;

    public DeliveryOptionQueryService(DeliveryOptionQueryPort port) {
        this.port = port;
    }

    @Override
    public Map<DeliveryGroup, List<DeliveryOptionReadModel>> getAllCategorized() {
        return port.findEnabled().stream()
                .collect(Collectors.groupingBy(d ->
                                DeliveryGroup.determineGroupFromName(d.name()),
                        () -> new EnumMap<>(DeliveryGroup.class),
                        Collectors.toList()
                ));
    }

    @Override
    public List<DeliveryOptionReadModel> getAllNotCategorized() {
        return port.findAll();
    }

    @Override
    public DeliveryOptionReadModel getById(Long id) {
        return port.findByIdReadModel(id).orElseThrow(
                () -> new ResourceNotFoundException("Not found"));
    }
}
