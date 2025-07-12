package com.example.courier.dto.response.person;

import com.example.courier.dto.AddressDTO;

public sealed interface PersonResponseDTO permits UserResponseDTO, AdminPersonResponseDTO, AdminProfileResponseDTO {
    String name();
    String email();
    String phoneNumber();
    AddressDTO defaultAddress();
}
