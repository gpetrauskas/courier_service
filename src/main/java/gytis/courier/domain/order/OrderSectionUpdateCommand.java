package gytis.courier.domain.order;

public record OrderSectionUpdateCommand(
        OrderStatus status,
        String deliveryMethodName
) {
}
