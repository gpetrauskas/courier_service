package com.example.courier.service;

import com.example.courier.common.ParcelStatus;
import com.example.courier.service.parcel.ParcelService;
import org.springframework.stereotype.Service;

@Service
public class TrackingService {

    private final ParcelService parcelService;

    public TrackingService(ParcelService parcelService) {
        this.parcelService = parcelService;
    }

    public ParcelStatus getParcelStatus(String trackingNumber) {
            return parcelService.getParcelStatusByTrackingNumber(trackingNumber);
    }
}
