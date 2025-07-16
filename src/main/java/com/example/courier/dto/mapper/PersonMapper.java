package com.example.courier.dto.mapper;

import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import com.example.courier.dto.CourierDTO;
import com.example.courier.dto.request.PersonDetailsUpdateRequest;
import com.example.courier.dto.response.person.AdminPersonResponseDTO;
import com.example.courier.dto.response.person.AdminProfileResponseDTO;
import com.example.courier.dto.response.person.UserResponseDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PersonMapper {

    @Named("toAdminPersonDto")
    @Mapping(target = "isBlocked", source = "blocked")
    AdminPersonResponseDTO toAdminPersonResponseDTO(Person person);

    CourierDTO toCourierDTO(Person person);

    @Mapping(target = "confirmedOrdersCount", source = "confirmedCount")
    UserResponseDTO toUserResponseDTO(User person, int confirmedCount);

    AdminProfileResponseDTO toAdminProfile(Person person);
}
