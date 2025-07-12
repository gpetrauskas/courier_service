package com.example.courier.dto.response.person;

import com.example.courier.dto.AddressDTO;

public record AdminProfileResponseDTO(
        String name,
        String email,
        String phoneNumber,
        AddressDTO defaultAddress,
        int tasksCreated,
        int ticketsSorted
) implements PersonResponseDTO {

}
