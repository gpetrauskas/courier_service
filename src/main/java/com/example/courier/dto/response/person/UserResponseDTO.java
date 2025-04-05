package com.example.courier.dto.response.person;

import com.example.courier.dto.AddressDTO;

public record UserResponseDTO(
        String name,
        String email,
        String phoneNumber,
        AddressDTO defaultAddress,
        boolean subscribed,
        int ordersMade
) implements PersonResponseDTO {
}
