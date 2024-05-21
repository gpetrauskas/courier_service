package com.example.courier.dto;

import com.example.courier.common.PackageStatus;

public record PackageDTO(Long id, String weight, String dimensions, String contents,
                         String trackingNumber, PackageStatus status) {
}
