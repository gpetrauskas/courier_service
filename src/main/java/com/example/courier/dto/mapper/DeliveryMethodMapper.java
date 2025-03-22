package com.example.courier.dto.mapper;

import com.example.courier.domain.DeliveryMethod;
import com.example.courier.dto.request.deliverymethod.CreateDeliveryMethodDTO;
import com.example.courier.dto.request.deliverymethod.UpdateDeliveryMethodDTO;
import com.example.courier.dto.response.deliverymethod.DeliveryMethodAdminResponseDTO;
import com.example.courier.dto.response.deliverymethod.DeliveryMethodUserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DeliveryMethodMapper {

  @Mapping(target = "id", ignore = true)
  void updateDeliveryOptionFromDTO(UpdateDeliveryMethodDTO deliveryOptionDTO, @MappingTarget DeliveryMethod deliveryMethod);

  DeliveryMethodUserResponseDTO toUserDeliveryOptionResponseDTO(DeliveryMethod deliveryMethod);
  DeliveryMethodAdminResponseDTO toAdminDeliveryOptionResponseDTO(DeliveryMethod deliveryMethod);

  DeliveryMethod toNewEntity(CreateDeliveryMethodDTO createDeliveryMethodDTO);
}
