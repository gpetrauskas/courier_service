package gytis.courier.adapter.in.rest.delivery;

import gytis.courier.adapter.in.rest.delivery.dto.*;
import gytis.courier.adapter.out.persistence.delivery.projection.DeliveryOptionProjection;
import gytis.courier.application.service.delivery.CreateDeliveryOptionCommand;
import gytis.courier.application.service.delivery.UpdateDeliveryOptionCommand;
import gytis.courier.domain.delivery.DeliveryGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DeliveryOptionMapper {
    // admin
    List<DeliveryOptionAdminResponse> toAdminResponse(List<DeliveryOptionProjection> list);
    DeliveryOptionAdminResponse toAdminResponse(DeliveryOptionProjection projection);

    //user
    DeliveryOptionResponse toResponse(DeliveryOptionProjection projection);
    default List<DeliveryOptionResponse> toResponseList(List<DeliveryOptionProjection> projections) {
        if (projections == null) return List.of();
        return projections.stream()
                .map(this::toResponse)
                .toList();
    }

    default DeliveryOptionsResponse toWrappedResponse(Map<DeliveryGroup, List<DeliveryOptionProjection>> grouped) {
        Map<DeliveryGroup, List<DeliveryOptionResponse>> map = grouped.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> toResponseList(e.getValue()),
                        (a, b) -> b,
                        () -> new EnumMap<>(DeliveryGroup.class)
                ));

        return new DeliveryOptionsResponse(map);
    }

    //command
    @Mapping(target = "id", source = "id")
    UpdateDeliveryOptionCommand toUpdateCommand(Long id, UpdateDeliveryMethodRequest request);

    CreateDeliveryOptionCommand toCreateCommand(CreateDeliveryOptionRequest request);
}
