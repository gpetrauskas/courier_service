package gytis.courier.application.readmodel.address;

public record AddressReadModel(
        Long id,
        String name,
        String street,
        String houseNumber,
        String flatNumber,
        String city,
        String postCode,
        String phoneNumber
) {
}
