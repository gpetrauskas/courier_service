package com.example.courier.service.parcel;

import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.Parcel;
import com.example.courier.dto.mapper.ParcelMapper;
import com.example.courier.dto.request.order.ParcelSectionUpdateRequest;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.ParcelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParcelService {

    private final ParcelRepository parcelRepository;
    private final ParcelMapper parcelMapper;

    public ParcelService(ParcelRepository parcelRepository, ParcelMapper parcelMapper) {
        this.parcelRepository = parcelRepository;
        this.parcelMapper = parcelMapper;
    }

    public void parcelSectionUpdate(ParcelSectionUpdateRequest updateRequest) {
        Parcel parcel = fetchById(updateRequest.id());
        ParcelStatus.validateStatus(updateRequest.status());
        parcelMapper.updateParcelSectionFromRequest(updateRequest, parcel);
        parcelRepository.save(parcel);
    }

    private Parcel fetchById(Long id) {
        return parcelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parcel was not found"));
    }

    public List<Parcel> fetchParcelsByIdBatch(List<Long> ids) {
        List<Parcel> parcels = parcelRepository.findAllById(ids);
        if (parcels.isEmpty()) {
            return List.of();
        }

        return parcels;
    }

    public Long getAvailableItemsCountByStatus(ParcelStatus status) {
        return parcelRepository.countByStatusAndIsAssignedFalse(status);
    }
}
