package gytis.courier.adapter.out.persistence.address;

import gytis.courier.adapter.out.persistence.address.projection.AddressProjection;
import gytis.courier.adapter.out.persistence.order.projection.OrderAddressDetailsProjection;
import gytis.courier.adapter.out.persistence.order.projection.OrderAddressProjection;
import gytis.courier.application.readmodel.address.AddressReadModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressReadModelMapper {
    AddressReadModel toReadModel(AddressProjection projection);


    default AddressReadModel toReadModel(OrderAddressProjection p) {
        if (p == null) return null;
        OrderAddressDetailsProjection d = p.getDetailsJpa();
        return new AddressReadModel(
                p.getId(),
                d.getName(),
                d.getStreet(),
                d.getHouseNumber(),
                d.getFlatNumber(),
                d.getCity(),
                d.getPostCode(),
                d.getPhoneNumber()
        );
    }

}