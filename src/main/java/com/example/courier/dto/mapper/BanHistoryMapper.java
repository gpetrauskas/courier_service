package com.example.courier.dto.mapper;

import com.example.courier.domain.BanHistory;
import com.example.courier.dto.response.BanHistoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BanHistoryMapper {
    BanHistoryDTO toDTO(BanHistory banHistory);
}
