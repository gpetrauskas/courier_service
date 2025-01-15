package com.example.courier.dto.mapper;

import com.example.courier.domain.Person;
import com.example.courier.dto.PersonDetailsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    PersonDetailsDTO toPersonDetailsDTO(Person person);
}
