package com.example.courier.service.person.strategy;

import com.example.courier.common.OrderStatus;
import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import com.example.courier.dto.UserWithOrdersCountByStatus;
import com.example.courier.dto.mapper.PersonMapper;
import com.example.courier.dto.response.person.PersonResponseDTO;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserInfoStrategy implements PersonInfoStrategy {
    private final UserRepository userRepository;
    private final PersonMapper personMapper;

    public UserInfoStrategy(UserRepository userRepository, PersonMapper personMapper) {
        this.userRepository = userRepository;
        this.personMapper = personMapper;
    }

    @Override
    public boolean supports(Person person) {
        return (person instanceof User);
    }

    @Override
    public PersonResponseDTO map(Person person) {
        UserWithOrdersCountByStatus user = userRepository.findUserWithOrdersCountByStatus(
                person.getId(), OrderStatus.CONFIRMED).orElseThrow(() ->
                new ResourceNotFoundException("User not found")
        );

        return personMapper.toUserResponseDTO(user.user(), user.confirmedOrdersCount());
    }
}
