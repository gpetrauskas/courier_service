package gytis.courier.adapter.out.persistence.person;

import gytis.courier.adapter.common.CommonValueObjectMapper;
import gytis.courier.adapter.out.persistence.address.common.AddressDetailsJpaMapper;
import gytis.courier.adapter.out.persistence.address.AddressEntityMapper;
import gytis.courier.adapter.out.persistence.paymentmethod.PaymentMethodMapper;
import gytis.courier.adapter.out.persistence.person.admin.AdminJpaEntity;
import gytis.courier.adapter.out.persistence.person.common.PersonJpaEntity;
import gytis.courier.adapter.out.persistence.person.courier.CourierJpaEntity;
import gytis.courier.adapter.out.persistence.person.user.UserJpaEntity;
import gytis.courier.domain.person.*;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {
                CommonValueObjectMapper.class,
                PaymentMethodMapper.class,
                AddressDetailsJpaMapper.class,
                AddressEntityMapper.class
        }
)
public interface PersonEntityMapper {

    //updating
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hasActiveTask", source = "hasActiveTask")
    void updateCourier(Courier courier, @MappingTarget CourierJpaEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentMethods", ignore = true)
    void updateEntityFromDomain(User user, @MappingTarget UserJpaEntity entity);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDomain(Person person, @MappingTarget PersonJpaEntity entity);


    // to domain
    @Named("resolvePersonDomain")
    default Person toSpecificBasicDomain(PersonJpaEntity entity) {
        if (entity instanceof UserJpaEntity u) { return toDomain(u); }
        if (entity instanceof AdminJpaEntity a) { return toDomain(a); }
        if (entity instanceof CourierJpaEntity c) { return toDomain(c); }
        throw new IllegalArgumentException("Unknown person type: " + entity.getClass());
    }

    @Mapping(target = "paymentMethods", ignore = true)
    User toDomain(UserJpaEntity entity);
    Admin toDomain(AdminJpaEntity entity);
    default Courier toDomain(CourierJpaEntity e) {
        return Courier.restore(
                e.getId(),
                e.getName(),
                new Email(e.getEmail()),
                e.getPassword(),
                new PhoneNumber(e.getPhoneNumber()),
                e.isBlocked(),
                e.isDeleted(),
                e.getDeletedDate(),
                e.hasActiveTask()
        );
    }

    @Mapping(target = "paymentMethods", qualifiedByName = "resolvePaymentMethodDomainList")
    User toDomainWithPaymentMethods(UserJpaEntity entity);

    @Mapping(target = "paymentMethods", ignore = true)
    User toDomainWithAddresses(UserJpaEntity entity);

// to entity
    @Named("resolvePersonJpa")
    default PersonJpaEntity toSpecificJpaEntity(Person domain) {
        if (domain instanceof User u) { return toJpaEntity(u); }
        if (domain instanceof Admin a) { return toJpaEntity(a); }
        if (domain instanceof Courier c) { return toJpaEntity(c); }
        throw new IllegalArgumentException("Unknown person type");
    }
    @Mapping(target = "paymentMethods", ignore = true)
    UserJpaEntity toJpaEntity(User domain);

    CourierJpaEntity toJpaEntity(Courier domain);

    AdminJpaEntity toJpaEntity(Admin domain);
}
