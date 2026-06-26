/*
package com.example.courier.adapter.out.persistence.order.projection.deletelater;

import projection.person.persistence.out.adapter.gytis.courier.CourierProjection;
import projection.person.persistence.out.adapter.gytis.courier.UserProjection;
import person.readmodel.application.gytis.courier.CourierReadModel;
import person.readmodel.application.gytis.courier.UserReadModel;
import org.springframework.stereotype.Component;

@Component
public class PersonReadModelMapper {
    public UserReadModel toUser(UserProjection projection) {
        return new UserReadModel(
                projection.getId(),
                projection.getName(),
                projection.getEmail(),
                projection.getPhoneNumber(),
                projection.isBlocked(),
                projection.isDeleted(),
                projection.getDeletedDate()
        );
    }

    public CourierReadModel toCourier(CourierProjection projection) {
        return new CourierReadModel(
                projection.getId(),
                projection.getName(),
                projection.getEmail(),
                projection.getPhoneNumber(),
                projection.isBlocked()
        );
    }
}
*/
