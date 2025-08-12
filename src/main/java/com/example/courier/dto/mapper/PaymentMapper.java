package com.example.courier.dto.mapper;

import com.example.courier.domain.Payment;
import com.example.courier.dto.request.order.PaymentSectionUpdateRequest;
import com.example.courier.dto.response.AdminPaymentResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    void updatePaymentSectionFromRequest(PaymentSectionUpdateRequest updateRequest, @MappingTarget Payment payment);

    AdminPaymentResponseDTO toAdminPaymentDTO(Payment payment);
}
