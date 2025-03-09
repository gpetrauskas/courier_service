package com.example.courier.dto;

import com.example.courier.common.ParcelStatus;

public record ParcelDTO(Long id, String weight, String dimensions, String contents,
                        String trackingNumber, ParcelStatus status) {
}
