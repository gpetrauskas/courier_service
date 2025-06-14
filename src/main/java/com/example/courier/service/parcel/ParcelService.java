package com.example.courier.service.parcel;

import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.Parcel;
import com.example.courier.dto.mapper.ParcelMapper;
import com.example.courier.dto.request.order.ParcelSectionUpdateRequest;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.ParcelRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        parcelMapper.updateParcelSectionFromRequest(updateRequest, parcel);
        parcelRepository.save(parcel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Long> getAvailableParcelsCount() {
        List<ParcelStatus> statuses = List.of(ParcelStatus.PICKING_UP, ParcelStatus.PICKED_UP);
        return statuses.stream()
                .collect(Collectors.toMap(
                        (status -> status.name().toLowerCase()),
                        this::getAvailableItemsCountByStatus
                ));
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
