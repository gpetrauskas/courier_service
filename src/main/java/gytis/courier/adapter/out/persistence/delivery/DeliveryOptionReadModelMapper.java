package gytis.courier.adapter.out.persistence.delivery;

import gytis.courier.adapter.out.persistence.delivery.projection.DeliveryOptionProjection;
import gytis.courier.application.readmodel.deliveryoption.DeliveryOptionReadModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeliveryOptionReadModelMapper {
    DeliveryOptionReadModel toReadModel(DeliveryOptionProjection projection);
}
