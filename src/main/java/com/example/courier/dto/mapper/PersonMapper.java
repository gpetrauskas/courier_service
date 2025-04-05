package com.example.courier.dto.mapper;

import com.example.courier.domain.Person;
import com.example.courier.dto.CourierDTO;
import com.example.courier.dto.request.PersonDetailsUpdateRequest;
import com.example.courier.dto.response.person.AdminPersonResponseDTO;
import com.example.courier.dto.response.person.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PersonMapper {

    @Mapping(target = "id", ignore = true)
    void updatePersonFromRequest(PersonDetailsUpdateRequest updateRequest, @MappingTarget Person person);

    AdminPersonResponseDTO toPersonResponseDTO(Person person);

    CourierDTO toCourierDTO(Person person);

    UserResponseDTO toPersonResponseNew (Person person);
}
