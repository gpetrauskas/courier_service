package com.example.courier.service;

import com.example.courier.dto.mapper.ParcelMapper;
import com.example.courier.repository.ParcelRepository;
import org.springframework.stereotype.Service;

@Service
public class ParcelService {

    private final ParcelRepository parcelRepository;
    private final ParcelMapper parcelMapper;

    public ParcelService(ParcelRepository parcelRepository, ParcelMapper parcelMapper) {
        this.parcelRepository = parcelRepository;
        this.parcelMapper = parcelMapper;
    }
}
