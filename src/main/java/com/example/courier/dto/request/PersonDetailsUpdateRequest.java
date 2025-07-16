package com.example.courier.dto.request;

import com.example.courier.validation.shared.AtLeastOneField;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@AtLeastOneField
public record PersonDetailsUpdateRequest(
        @Size(max = 30) String name,
        @Email @Size(max = 40) String email,
        @Size(max = 8) String phoneNumber
) {
}
