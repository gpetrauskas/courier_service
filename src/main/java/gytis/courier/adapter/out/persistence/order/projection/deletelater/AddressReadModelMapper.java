/*
package com.example.courier.adapter.out.persistence.order.projection.deletelater;

import com.example.courier.adapter.out.persistence.address.query.projection.AddressDetailsProjection;
import projection.address.persistence.out.adapter.gytis.courier.AddressProjection;
import address.readmodel.application.gytis.courier.AddressReadModel;
import org.springframework.stereotype.Component;

@Component
public class AddressReadModelMapper {
    public AddressReadModel toAddress(AddressProjection projection) {
        return new AddressReadModel(
                projection.getId(),
                projection.getDetails().getName(),
                projection.getDetails().getStreet(),
                projection.getDetails().getHouseNumber(),
                projection.getDetails().getFlatNumber(),
                projection.getDetails().getCity(),
                projection.getDetails().getPostCode(),
                projection.getDetails().getPhoneNumber()
        );
    }

    public String toFullAddress(AddressProjection projection) {
        AddressDetailsProjection a = projection.getDetails();
        String flatIfExists = (a.getFlatNumber() != null && a.getFlatNumber().isBlank())
                ? "/" + a.getFlatNumber() + ", "
                : ", ";

        return a.getStreet() + " " + a.getHouseNumber() + flatIfExists
                + a.getCity() + " " + a.getPostCode()
                + " " + a.getName() + " " + a.getPhoneNumber();
    }
}
*/
