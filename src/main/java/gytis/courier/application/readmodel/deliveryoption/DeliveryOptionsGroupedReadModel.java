package gytis.courier.application.readmodel.deliveryoption;

import gytis.courier.domain.delivery.DeliveryGroup;

import java.util.EnumMap;
import java.util.List;

public record DeliveryOptionsGroupedReadModel(
        EnumMap<DeliveryGroup, List<DeliveryOptionReadModel>> grouped
) {
}
