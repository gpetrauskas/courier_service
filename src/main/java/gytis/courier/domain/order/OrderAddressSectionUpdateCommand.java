package gytis.courier.domain.order;

public record OrderAddressSectionUpdateCommand(
        String selectedAddress,
        Long id,
        String city,
        String street,
        String houseNumber,
        String flatNumber,
        String phoneNumber,
        String postCode,
        String name
) {
}
