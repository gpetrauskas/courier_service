package gytis.courier.domain.order;

import gytis.courier.domain.address.AddressDetails;

public class OrderAddress {
    private AddressDetails details;

    protected OrderAddress() {}
    public OrderAddress(AddressDetails details) {
        this.details = details;
    }

    public AddressDetails getDetails() { return details; }

    public void update(OrderAddressSectionUpdateCommand command) {
        this.details = new AddressDetails(
                (command.name() != null) ? command.name() : this.details.getName(),
                (command.street() != null) ? command.street() : this.details.getStreet(),
                (command.houseNumber() != null) ? command.houseNumber() : this.details.getHouseNumber(),
                (command.flatNumber() != null) ? command.flatNumber() : this.details.getFlatNumber(),
                (command.city() != null) ? command.city() : this.details.getCity(),
                (command.postCode() != null) ? command.postCode() : this.details.getPostCode(),
                (command.phoneNumber() != null) ? command.phoneNumber() : this.details.getPhoneNumber()
        );
    }

    public static OrderAddress from(AddressDetails addressDetails) {
        return new OrderAddress(addressDetails);
    }
}