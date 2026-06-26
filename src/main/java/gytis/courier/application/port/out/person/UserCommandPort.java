package gytis.courier.application.port.out.person;

import gytis.courier.domain.person.User;

import java.util.Optional;

public interface UserCommandPort {
    Optional<User> findWithPaymentMethodsById(Long id);
    Optional<User> findWithAddressesById(Long id);
    Optional<User> findWithOrdersById(Long id);
    Optional<User> findWithAllDataById(Long id);
    Optional<User> findById(Long id);
    void save(User user);
    void create(User user);
    void saveWithPaymentMethods(User user);
    void deletePaymentMethod(Long methodId, Long userId);
}
