package com.example.courier.dto.request.person;

public record UserEditDTO(
        String phoneNumber,
        Long defaultAddressId,
        Boolean subscribed
) {
}
