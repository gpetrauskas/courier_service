package gytis.courier.domain.address;

import gytis.courier.application.command.PartialAddressUpdateCommand;

import java.util.Objects;

public class Address {
    private Long id;
    private Long userId;
    private AddressDetails details;

    protected Address() {}

    public Address(Long userId, AddressDetails details) {
        this.userId = userId;
        this.details = details;
    }

    public Address(Long id, Long userId, AddressDetails details) {
        this.id = id;
        this.userId = userId;
        this.details = details;
    }

    public static Address restore(Long id, AddressDetails details) {
        Address address = new Address();
        address.id = id;
        address.details = details;

        return address;
    }

    public Long getUserId() { return userId; }
    public Long getId() { return id; }
    public AddressDetails getDetails() { return details; }
    public void updateAddress(AddressDetails details) { this.details = details; }

    public void partialUpdate(PartialAddressUpdateCommand update) {
        Objects.requireNonNull(update);

        AddressDetails newDetails = AddressDetails.createValidated(
                keepOldIfNull(update.name(), details.getName()),
                keepOldIfNull(update.street(), details.getStreet()),
                keepOldIfNull(update.houseNumber(), details.getHouseNumber()),
                keepOldIfNull(update.flatNumber(), details.getFlatNumber()),
                keepOldIfNull(update.city(), details.getCity()),
                keepOldIfNull(update.postCode(), details.getPostCode()),
                keepOldIfNull(update.phoneNumber(), details.getPhoneNumber())
        );

        updateAddress(newDetails);
    }

    private <T> T keepOldIfNull(T newValue, T oldValue) {
        return newValue != null ? newValue : oldValue;
    }
}
