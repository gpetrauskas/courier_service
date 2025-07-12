package com.example.courier.service.person.strategy;

import com.example.courier.common.OrderStatus;
import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import com.example.courier.dto.mapper.PersonMapper;
import com.example.courier.dto.response.person.PersonResponseDTO;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.OrderRepository;
import com.example.courier.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserInfoStrategy implements PersonInfoStrategy {
    private final UserRepository userRepository;
    private final PersonMapper personMapper;
    private final OrderRepository orderRepository;

    public UserInfoStrategy(UserRepository userRepository, PersonMapper personMapper, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.personMapper = personMapper;
        this.orderRepository = orderRepository;
    }

    @Override
    public boolean supports(Person person) {
        return (person instanceof User);
    }

    @Override
    public PersonResponseDTO map(Person person) {
        User user = userRepository.findById(person.getId()).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));
        int confirmedOrdersCount = orderRepository.countByUserIdAndStatus(user.getId(), OrderStatus.CONFIRMED);
        return personMapper.toUserResponseDTO(user, confirmedOrdersCount);
    }
}
