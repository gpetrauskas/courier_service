package com.example.courier.service.person.strategy;

import com.example.courier.domain.Person;
import com.example.courier.dto.response.person.PersonResponseDTO;
import com.example.courier.exception.StrategyNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonInfoStrategyResolver {
    private static final Logger logger = LoggerFactory.getLogger(PersonInfoStrategyResolver.class);
    private final List<PersonInfoStrategy> strategies;

    public PersonInfoStrategyResolver(List<PersonInfoStrategy> strategies) {
        this.strategies = strategies;
    }

    public PersonResponseDTO resolve(Person person) {
        PersonInfoStrategy strategy = strategies.stream()
                .filter(s -> s.supports(person))
                .findFirst()
                .orElseThrow(() -> new StrategyNotFoundException("Strategy not found for person: " + person.getId()));

        logger.info("Strategy selected: {}", strategy.getClass().getSimpleName());
        return strategy.map(person);
    }

}
