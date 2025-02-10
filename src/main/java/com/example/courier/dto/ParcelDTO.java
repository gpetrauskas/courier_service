package com.example.courier.dto;

import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.Parcel;

public record ParcelDTO(Long id, String weight, String dimensions, String contents,
                        String trackingNumber, ParcelStatus status) {

    public static ParcelDTO parcelToDTO(Parcel pack) {
        return new ParcelDTO(pack.getId(), pack.getWeight(), pack.getDimensions(), pack.getContents(),
                pack.getTrackingNumber(), pack.getStatus());
    }
}
