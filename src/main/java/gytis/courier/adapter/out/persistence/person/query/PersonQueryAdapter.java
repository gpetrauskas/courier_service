package gytis.courier.adapter.out.persistence.person.query;

import gytis.courier.adapter.out.persistence.common.PageResultMapper;
import gytis.courier.adapter.out.persistence.common.PageableFactory;
import gytis.courier.adapter.out.persistence.person.admin.AdminJpaRepository;
import gytis.courier.adapter.out.persistence.person.common.PersonJpaEntity;
import gytis.courier.adapter.out.persistence.person.common.PersonJpaRepository;
import gytis.courier.adapter.out.persistence.person.common.PersonSpecificationBuilder;
import gytis.courier.adapter.out.persistence.person.courier.CourierJpaRepository;
import gytis.courier.adapter.out.persistence.person.user.PersonInfoReadModelMapper;
import gytis.courier.adapter.out.persistence.person.user.UserJpaRepository;
import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.out.auth.PersonQueryPort;
import gytis.courier.application.query.filter.PersonQuery;
import gytis.courier.application.readmodel.person.*;
import gytis.courier.domain.person.Role;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PersonQueryAdapter implements PersonQueryPort {
    private final AdminJpaRepository adminRepo;
    private final CourierJpaRepository courierRepo;
    private final UserJpaRepository userRepo;
    private final PersonJpaRepository personRepo;
    private final PersonInfoReadModelMapper mapper;


    public PersonQueryAdapter(AdminJpaRepository adminRepo, CourierJpaRepository courierRepo, UserJpaRepository userRepo, PersonJpaRepository personRepo, PersonInfoReadModelMapper mapper) {
        this.adminRepo = adminRepo;
        this.courierRepo = courierRepo;
        this.userRepo = userRepo;
        this.personRepo = personRepo;
        this.mapper = mapper;
    }

    @Override
    public Optional<MyInfoReadModel> getMyInfo(Long id, String role) {
        return switch (role.toUpperCase()) {
            case "USER" -> userRepo.getUserInfo(id).map(mapper::toReadModel);
            case "ADMIN" -> adminRepo.getAdminInfo(id).map(mapper::toReadModel);
            case "COURIER" -> courierRepo.getCourierInfo(id).map(mapper::toReadModel);
            default -> Optional.empty();
        };
    }

    @Override
    public PageResult<AdminPersonListReadModel> getAll(PersonQuery query, PageQuery pageQuery) {
        Specification<PersonJpaEntity> specification = PersonSpecificationBuilder.buildPersonSpecification(query.role(), query.keyword());
        Pageable pageable = PageableFactory.from(pageQuery);

        return PageResultMapper.from(
                personRepo.findAll(specification, pageable),
                mapper::toAdminList
        );
    }

    @Override
    public Optional<Role> findPersonRole(Long id) {
        return personRepo.findRoleById(id).map(Role::valueOf);
    }

    @Override
    public Optional<AdminUserDetailsReadModel> getAdminUserDetailed(Long id) {
        return userRepo.getAdminUserDetailsById(id).map(mapper::toAdminDetailed);
    }

    @Override
    public Optional<AdminCourierDetailsReadModel> getAdminCourierDetailed(Long id) {
        return courierRepo.getAdminDetailedById(id).map(mapper::toAdminDetailed);
    }

    @Override
    public Optional<AdminAdminDetailsReadModel> getAdminAdminDetailed(Long id) {
        return adminRepo.getAdminDetailedById(id).map(mapper::toAdminDetailed);
    }

    @Override
    public List<Long> getAllActiveUserIds(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepo.findAllActiveIds(pageable);
    }

    @Override
    public List<Long> getAllActiveCourierIds(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return courierRepo.findAllActiveIds(pageable);
    }

    @Override
    public List<Long> getAllActiveAdminIds(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return adminRepo.findAllActiveIds(pageable);
    }

    @Override
    public boolean existsByEmail(String email) {
        return personRepo.existsByEmail(email);
    }
}
