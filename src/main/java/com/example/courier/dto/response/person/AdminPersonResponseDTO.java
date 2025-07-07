package com.example.courier.dto.response.person;

import com.example.courier.dto.AddressDTO;

public record AdminPersonResponseDTO(
        String name,
        String email,
        String phoneNumber,
        AddressDTO defaultAddress,
        Long id,
        boolean isDeleted,
        boolean isBlocked,
        String lastTimeActive
) implements PersonResponseDTO {
}
