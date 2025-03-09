package com.example.courier.dto.mapper;

import com.example.courier.domain.Payment;
import com.example.courier.dto.AdminPaymentDTO;
import com.example.courier.dto.request.PaymentSectionUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    void updatePaymentSectionFromRequest(PaymentSectionUpdateRequest updateRequest, @MappingTarget Payment payment);

    AdminPaymentDTO toAdminPaymentDTO(Payment payment);
}
