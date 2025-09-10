package com.example.courier.service.person.query;

import com.example.courier.common.OrderStatus;
import com.example.courier.domain.Admin;
import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import com.example.courier.dto.UserWithOrdersCountByStatus;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.exception.UserNotFoundException;
import com.example.courier.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/** Service for retrieving {@link Person} and its subtypes.
 */
@Service
public class PersonLookupService {
    private final PersonRepository personRepository;

    public PersonLookupService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /** Find a {@link Person} by their username(email)
     *
     * @param username the email to search for.
     * @return the matching {@link Person}
     * @throws UserNotFoundException if no person found with the given email
     */
    public Person findByUsername(String username) {
        return personRepository.findByEmail(username).orElseThrow(() ->
                new UserNotFoundException("Person not found with username/email: " + username));
    }

    /** Retrieves the ids of all active persons of the specified subtype.
     *
     * @param type the concrete {@link Person} subtype to filter by
     * @return a list of ids with all active persons of given subtype
     * */
    public List<Long> findAllActiveIdsByType(Class<? extends Person> type) {
        return personRepository.findAllActiveIdsByType(type);
    }

    /** Retrieves a {@link Person} that is not soft deleted.
     *
     * @param id the id of the person to search for
     * @return a founded person entity
     */
    public Person findNotDeletedPerson(Long id) {
        return personRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("User was not found"));
    }

    /** Retrieves all persons of the specified subtype.
     *
     * @param personType the concrete {@link Person} subtype to retrieve
     * @param <T> the subtype of {@link Person}
     * @return a list of all persons of the given subtype
     */
    public <T extends Person> List<T> fetchAllByType(Class<T> personType) {
        return personRepository.findAllByType(personType);
    }

    /** Retrieves all active persons of the specified subtype.
     *
     * <p> Active means the person is not blocked not deleted.</p>
     *
     * @param tClass the concrete {@link Person} subtype to retrieve.
     * @param <T> the subtype of {@link Person}
     * @return a list of all active persons of the given subtype*/
    public <T extends Person> List<T> getAllActiveByType(Class<T> tClass) {
        return personRepository.findAllActiveByType(tClass);
    }

    /** Fetches specified entity of the given subtype and id.
     *
     * @param id of the person to be found
     * @param personType the concrete subtype to retrieve
     * @param <T> the subtype of {@link Person}
     * @return the person of the given subtype and ID
     * @throws ResourceNotFoundException if no person is found by given ID
     * @throws IllegalArgumentException if the found person is not  of expected subtype
     */
    public <T extends Person> T fetchPersonByIdAndType(Long id, Class<T> personType) {
        Person person = fetchById(id);
        return convertToType(person, personType);
    }

    /** Fetches a {@link Person} by the given ID.
     *
     * @param personId the ID of the person to retrieve
     * @return the person of the given ID
     * @throws ResourceNotFoundException if person is not found by the given ID
     */
    public Person fetchById(Long personId) {
        return personRepository.findById(personId).orElseThrow(() ->
                new ResourceNotFoundException("User was not found."));
    }

    /** Checks whether a person with the given email already exists.
     *
     * @param email an email address to check
     * @return {@code true} if email already exists and {@code false} otherwise
     */
    public boolean checkIfPersonAlreadyExistsByEmail(String email) {
        return personRepository.existsByEmail(email);
    }

    /** Checks if person by given ID is active and exists.
     * <p> Active means that person is not blocked and not deleted.</p>
     *
     * @param id of the person to check
     * @return {@code true} if the person exists and is active and {@code false} otherwise
     */
    public boolean existsByIdAndIsActive(Long id) {
        return personRepository.existsByIdAndIsBlockedFalseAndIsDeletedFalse(id);
    }

    /** Fetches a {@link User} entity with its addresses eagerly loaded by the given user id.
     *
     * @param userId an ID of the user to be fetched
     * @return the user with its addresses loaded
     * @throws ResourceNotFoundException if user is not found
     */
    public User findUserByIdWithAddresses(Long userId) {
        return personRepository.findUserByIdWithAddresses(userId).orElseThrow(() ->
                new ResourceNotFoundException("User was not found"));
    }

    /** Fetches a {@link UserWithOrdersCountByStatus} projection by given user ID and order status.
     *
     * @param personId the ID of the user to retrieve
     * @param status the order status to filter by
     * @return a {@link UserWithOrdersCountByStatus} containing the user and their
     *              order cound for the given status
     * @throws ResourceNotFoundException if no user found with the given ID
     */
    public UserWithOrdersCountByStatus findUserWithOrdersCountByStatus(Long personId, OrderStatus status) {
        return personRepository.findUserWithOrdersCountByStatus(personId, status)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")
        );
    }

    /* Helper methods
    */

    private <T extends Person> T convertToType(Person person, Class<T> personType) {
        return Optional.of(person)
                .filter(personType::isInstance)
                .map(personType::cast)
                .orElseThrow(() -> new IllegalArgumentException("The person is no instance of " + personType.getSimpleName()));
    }
}
