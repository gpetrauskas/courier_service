package com.example.courier.dto.mapper;

import com.example.courier.domain.User;
import com.example.courier.dto.UserDetailsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDetailsDTO toUserDetailsDTO(User user);
}
