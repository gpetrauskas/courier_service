package com.example.courier.service.person.strategy;

import com.example.courier.common.Role;
import com.example.courier.domain.Person;
import com.example.courier.dto.response.person.PersonResponseDTO;
import com.example.courier.exception.StrategyNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/** Resolves the appropriate {@link PersonInfoStrategy} implementation for a give {@link Person}
 * based on their {@link Role} and delegates the mapping to a {@link PersonResponseDTO}.
 *
 * This class uses the strategy pattern to select the correct mapping logic at runtime.
 * All available strategies are injected by spring via constructor injection.
 */
@Component
public class PersonInfoStrategyResolver {
    private static final Logger logger = LoggerFactory.getLogger(PersonInfoStrategyResolver.class);
    private final List<PersonInfoStrategy> strategies;

    public PersonInfoStrategyResolver(List<PersonInfoStrategy> strategies) {
        this.strategies = strategies;
    }

    /** Resolves and executes the appropriate {@link PersonInfoStrategy} for the given {@link Person}.
     *
     * @param person the person whose information should be mapped
     * @return a {@link PersonResponseDTO} mapped for the given person
     * @throws StrategyNotFoundException if no strategy supports the persons role
     */
    public PersonResponseDTO resolve(Person person) {
        Role role = Role.valueOf(person.getRole().toUpperCase());

        logger.info("Searching strategy for role: {}", role);
        return strategies.stream()
                .filter(s -> s.supportsType() == role)
                .findFirst()
                .orElseThrow(() -> new StrategyNotFoundException("Strategy not found for person: " + person.getId()))
                .map(person.getId());
    }
}
