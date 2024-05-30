package com.example.courier.dto;

import com.example.courier.common.PackageStatus;
import com.example.courier.domain.Package;

public record PackageDTO(Long id, String weight, String dimensions, String contents,
                         String trackingNumber, PackageStatus status) {

    public static PackageDTO packageToDTO(Package pack) {
        return new PackageDTO(pack.getId(), pack.getWeight(), pack.getDimensions(), pack.getContents(),
                pack.getTrackingNumber(), pack.getStatus());
    }
}
