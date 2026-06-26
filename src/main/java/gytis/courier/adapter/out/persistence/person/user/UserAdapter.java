package gytis.courier.adapter.out.persistence.person.user;

import gytis.courier.adapter.out.persistence.paymentmethod.PaymentMethodJpaEntity;
import gytis.courier.adapter.out.persistence.paymentmethod.PaymentMethodJpaRepository;
import gytis.courier.adapter.out.persistence.paymentmethod.PaymentMethodMapper;
import gytis.courier.adapter.out.persistence.person.PersonEntityMapper;
import gytis.courier.application.port.out.person.UserCommandPort;
import gytis.courier.domain.person.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class UserAdapter implements UserCommandPort {
    private final UserJpaRepository repository;
    private final PaymentMethodJpaRepository paymentMethodJpaRepository;
    private final PaymentMethodMapper paymentMethodMapper;
    private final PersonEntityMapper mapper;

    public UserAdapter(UserJpaRepository repository, PaymentMethodJpaRepository paymentMethodJpaRepository, PaymentMethodMapper paymentMethodMapper, PersonEntityMapper mapper) {
        this.repository = repository;
        this.paymentMethodJpaRepository = paymentMethodJpaRepository;
        this.paymentMethodMapper = paymentMethodMapper;
        this.mapper = mapper;
    }

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findWithPaymentMethodsById(Long id) {
        return repository.findWithPaymentMethodsById(id)
                .map(mapper::toDomainWithPaymentMethods);
    }

    @Override
    public Optional<User> findWithAddressesById(Long id) {
        return repository.findWithAddressesById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findWithOrdersById(Long id) {
        return repository.findWithOrdersById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findWithAllDataById(Long id) {
        return repository.findWithAllDataById(id)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void save(User user) {
        UserJpaEntity managed = repository.findById(user.getId()).orElseThrow();
        mapper.updateEntityFromDomain(user, managed);

        System.out.println("adapter adddrs def " + user.getDefaultAddressId());
    }

    @Override
    @Transactional
    public void create(User user) {
        UserJpaEntity entity = mapper.toJpaEntity(user);
        repository.save(entity);
    }

    @Override
    @Transactional
    public void saveWithPaymentMethods(User user) {
        UserJpaEntity managed = repository.findWithPaymentMethodsById(user.getId()).orElseThrow();
        mapper.updateEntityFromDomain(user, managed);

        user.getPaymentMethods().stream()
                .filter(method -> method.getId() == null)
                .findFirst()
                .ifPresent(newMethod -> {
                    PaymentMethodJpaEntity newPaymentMethod = paymentMethodMapper.toSpecificEntity(newMethod);
                    newPaymentMethod.setUser(managed);
                    managed.getPaymentMethods().add(newPaymentMethod);
                });
    }

    @Override
    @Transactional
    public void deletePaymentMethod(Long methodId, Long userId) {
        paymentMethodJpaRepository.deleteByIdAndUserId(methodId, userId);
    }
}
