package gytis.courier.application.service.order;

public record AddressInput(
        String name,
        String street,
        String houseNumber,
        String flatNumber,
        String city,
        String postCode,
        String phoneNumber
) {
}
