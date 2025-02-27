package com.example.courier.dto.mapper;

import com.example.courier.domain.Person;
import com.example.courier.dto.PersonDetailsDTO;
import com.example.courier.dto.request.PersonDetailsUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    PersonDetailsDTO toPersonDetailsDTO(Person person);

    @Mapping(target = "id", ignore = true)
    void updatePersonFromRequest(PersonDetailsUpdateRequest updateRequest, @MappingTarget Person person);
}
