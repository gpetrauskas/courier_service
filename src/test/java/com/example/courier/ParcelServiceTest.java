package com.example.courier;

import com.example.courier.common.ParcelStatus;
import com.example.courier.domain.Parcel;
import com.example.courier.dto.mapper.ParcelMapper;
import com.example.courier.dto.request.order.ParcelSectionUpdateRequest;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.ParcelRepository;
import com.example.courier.service.parcel.ParcelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParcelServiceTest {

    @Mock private ParcelRepository parcelRepository;
    private final ParcelMapper mapper = Mappers.getMapper(ParcelMapper.class);

    @InjectMocks private ParcelService parcelService;

    private Parcel parcel;

    @BeforeEach
    void setup() {
        parcelService = new ParcelService(parcelRepository, mapper);

        parcel = new Parcel();
        parcel.setId(2L);
        parcel.setStatus(ParcelStatus.PICKING_UP);
        parcel.setContents("books");
    }

    @Test
    @DisplayName("should update parcel contents and status correctly")
    void shouldSuccessfullyUpdateParcel() {
        ParcelSectionUpdateRequest request = new ParcelSectionUpdateRequest(2L, "parcelSection", "PICKED_UP", "not books");

        when(parcelRepository.findById(2L)).thenReturn(Optional.of(parcel));

        parcelService.parcelSectionUpdate(request);

        assertThat(parcel)
                .returns("not books", Parcel::getContents)
                .returns(ParcelStatus.PICKED_UP, Parcel::getStatus);
        verify(parcelRepository).save(parcel);
    }

    @Test
    void shouldThrowWhenParcelStatusIsInvalid() {
        ParcelSectionUpdateRequest request = new ParcelSectionUpdateRequest(2L, "parcelSection", "invalid_status", "not books");

        when(parcelRepository.findById(2L)).thenReturn(Optional.of(parcel));

        assertThrows(IllegalArgumentException.class, () -> parcelService.parcelSectionUpdate(request));

        verify(parcelRepository, never()).save(parcel);
    }

    @Test
    void shouldThrow_whenParcelNotFound() {
        ParcelSectionUpdateRequest request = new ParcelSectionUpdateRequest(2L, "parcelSection", "PICKED_UP", "not books");

        when(parcelRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> parcelService.parcelSectionUpdate(request));

        verify(parcelRepository, never()).save(any());
    }
}
