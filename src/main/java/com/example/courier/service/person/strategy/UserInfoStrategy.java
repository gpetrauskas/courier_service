package com.example.courier.service.person.strategy;

import com.example.courier.common.OrderStatus;
import com.example.courier.common.Role;
import com.example.courier.domain.Person;
import com.example.courier.dto.UserWithOrdersCountByStatus;
import com.example.courier.dto.mapper.PersonMapper;
import com.example.courier.dto.response.person.PersonResponseDTO;
import com.example.courier.service.person.query.PersonLookupService;
import org.springframework.stereotype.Component;

@Component
public class UserInfoStrategy implements PersonInfoStrategy {
    private final PersonLookupService lookupService;
    private final PersonMapper personMapper;

    public UserInfoStrategy(PersonLookupService lookupService, PersonMapper personMapper) {
        this.lookupService = lookupService;
        this.personMapper = personMapper;
    }

    @Override
    public Role supportsType() {
        return Role.USER;
    }

    @Override
    public PersonResponseDTO map(Long personId) {
        UserWithOrdersCountByStatus user = lookupService.findUserWithOrdersCountByStatus(
                personId, OrderStatus.CONFIRMED);

        return personMapper.toUserResponseDTO(user.user(), user.confirmedOrdersCount());
    }
}
