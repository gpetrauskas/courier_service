package com.example.courier.dto;

public record PackageDTO(Long id, String weight, String dimensions, String contents,
                         String trackingNumber, String status) {
}
