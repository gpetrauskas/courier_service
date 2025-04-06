package com.example.courier.dto.request.person;

import java.util.Optional;

public record UserEditDTO(
        Optional<String> phoneNumber,
        Optional<Long> defaultAddressId,
        Optional<Boolean> subscribed
) {
}
