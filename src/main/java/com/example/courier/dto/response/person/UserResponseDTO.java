package com.example.courier.dto.response.person;

import com.example.courier.dto.AddressDTO;

import java.util.List;

public record UserResponseDTO(
        String name,
        String email,
        String phoneNumber,
        AddressDTO defaultAddress,
        List<AddressDTO> addresses,
        boolean subscribed,
        int confirmedOrdersCount
) implements PersonResponseDTO {
}
