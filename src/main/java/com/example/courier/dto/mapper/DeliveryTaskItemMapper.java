package com.example.courier.dto.mapper;

import com.example.courier.domain.DeliveryTaskItem;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DeliveryTaskItemMapper {

}
