package com.example.courier.dto.mapper;

import com.example.courier.domain.Admin;
import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import com.example.courier.dto.CourierDTO;
import com.example.courier.dto.request.PersonDetailsUpdateRequest;
import com.example.courier.dto.response.person.AdminPersonResponseDTO;
import com.example.courier.dto.response.person.PersonResponseDTO;
import com.example.courier.dto.response.person.UserResponseDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PersonMapper {

    @Mapping(target = "id", ignore = true)
    void updatePersonFromRequest(PersonDetailsUpdateRequest updateRequest, @MappingTarget Person person);

    @Named("toAdminPersonDto")
    AdminPersonResponseDTO toAdminPersonResponseDTO(Person person);

    CourierDTO toCourierDTO(Person person);

    @Named("toPersonDto")
    default PersonResponseDTO toDto(Person person) {
        if (person == null) {
            return null;
        }
        return switch (person) {
            case User u -> toUserResponseDTO(u);
            case Admin a -> toAdminPersonResponseDTO(a);
            default -> throw new IllegalArgumentException("Unknown type " + person.getClass());
        };
    }

    @Mapping(target = "confirmedOrdersCount", source = "confirmedOrdersCount")
    @Mapping(target = "subscribed", source = "subscribed")
    UserResponseDTO toUserResponseDTO(User person);
}
