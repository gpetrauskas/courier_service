package com.example.courier.dto.mapper;

import com.example.courier.domain.Admin;
import com.example.courier.domain.Courier;
import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import com.example.courier.dto.CourierDTO;
import com.example.courier.dto.PersonDetailsDTO;
import com.example.courier.dto.PersonResponseDTO;
import com.example.courier.dto.request.PersonDetailsUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PersonMapper {

    PersonDetailsDTO toPersonDetailsDTO(Person person);

    @Mapping(target = "id", ignore = true)
    void updatePersonFromRequest(PersonDetailsUpdateRequest updateRequest, @MappingTarget Person person);

    PersonResponseDTO toPersonResponseDTO(Person person);

    CourierDTO toCourierDTO(Person person);

    PersonDetailsDTO toUserDTO(Person person);
}
