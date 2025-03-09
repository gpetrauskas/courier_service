package com.example.courier.dto.mapper;

import com.example.courier.domain.PricingOption;
import com.example.courier.dto.PricingOptionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PricingOptionMapper {

  @Mapping(target = "id", ignore = true)
  void updatePricingOptionFromDTO(PricingOptionDTO pricingOptionDTO, @MappingTarget PricingOption pricingOption);
}
