package com.example.courier.dto.mapper;

import com.example.courier.domain.DeliveryOption;
import com.example.courier.dto.DeliveryOptionBaseDTO;
import com.example.courier.dto.request.deliveryoption.CreateDeliveryOptionDTO;
import com.example.courier.dto.request.deliveryoption.UpdateDeliveryOptionDTO;
import com.example.courier.dto.response.deliveryoption.DeliveryOptionAdminResponseDTO;
import com.example.courier.dto.response.deliveryoption.DeliveryOptionUserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DeliveryOptionMapper {

  @Mapping(target = "id", ignore = true)
  void updateDeliveryOptionFromDTO(UpdateDeliveryOptionDTO deliveryOptionDTO, @MappingTarget DeliveryOption deliveryOption);

  DeliveryOptionUserResponseDTO toUserDeliveryOptionResponseDTO(DeliveryOption deliveryOption);
  DeliveryOptionAdminResponseDTO toAdminDeliveryOptionResponseDTO(DeliveryOption deliveryOption);

  DeliveryOption toEntity(DeliveryOptionBaseDTO deliveryOptionBaseDTO);

  DeliveryOption toNewEntity(CreateDeliveryOptionDTO createDeliveryOptionDTO);
}
