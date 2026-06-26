package gytis.courier.application.readmodel.task;

import gytis.courier.domain.order.ParcelStatus;

public record CTaskItemReadModel(
        Long id,
        ParcelStatus status,
        String deliveryMethodName,
        String contents,
        String weight,
        String dimensions,
        String relevantAddress,
        String relevantContacts) {
}
