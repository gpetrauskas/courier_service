package gytis.courier.application.command;

public record PartialAddressUpdateCommand(
        String name,
        String street,
        String houseNumber,
        String flatNumber,
        String city,
        String postCode,
        String phoneNumber
) {}
