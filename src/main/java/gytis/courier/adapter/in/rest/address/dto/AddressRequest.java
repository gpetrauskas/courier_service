package gytis.courier.adapter.in.rest.address.dto;


import gytis.courier.adapter.in.rest.common.validation.ValidAddressRequest;

@ValidAddressRequest
public record AddressRequest(
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
