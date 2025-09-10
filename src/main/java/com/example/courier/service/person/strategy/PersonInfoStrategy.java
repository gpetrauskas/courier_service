package com.example.courier.service.person.strategy;

import com.example.courier.common.Role;
import com.example.courier.domain.Person;
import com.example.courier.dto.response.person.PersonResponseDTO;

/** Strategy interface for mapping {@link Person} information based on their {@link Role}.
 *
 * <p>Implementations of this interface handle a specific role type and provide
 * the logic to map a person entity to an appropriate {@link PersonResponseDTO}.</p>
 */
public interface PersonInfoStrategy {
    /** Returns the {@link Role} type that this strategy supports
     * This is used to determine whether the strategy can handle a given person.
     *
     * @return the supported {@link Role}
     */
    Role supportsType();

    /** Maps the person with the given ID to a {@link PersonResponseDTO}.
     *
     * @param personId the ID of the person to map
     * @return the mapped {@link PersonResponseDTO} for the given person
     */
    PersonResponseDTO map(Long personId);
}
