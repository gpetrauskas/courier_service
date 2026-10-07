package gytis.courier.config;

import gytis.courier.adapter.out.persistence.person.admin.AdminJpaEntity;
import gytis.courier.adapter.out.persistence.person.common.PersonJpaEntity;
import gytis.courier.adapter.out.persistence.person.common.PersonJpaRepository;
import gytis.courier.adapter.out.persistence.person.courier.CourierJpaEntity;
import gytis.courier.adapter.out.persistence.person.admin.AdminJpaRepository;
import gytis.courier.adapter.out.persistence.person.courier.CourierJpaRepository;
import gytis.courier.adapter.out.persistence.person.user.UserJpaEntity;
import gytis.courier.adapter.out.persistence.person.user.UserJpaRepository;
import gytis.courier.domain.person.Admin;
import gytis.courier.domain.person.Email;
import gytis.courier.domain.person.Person;
import gytis.courier.domain.person.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdminJpaRepository adminRepository;
    @Autowired
    private CourierJpaRepository courierRepository;
    @Autowired
    private PersonJpaRepository personJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String encodedPassword = passwordEncoder.encode("pass123");
        if (adminRepository.count() == 0) {
            createPerson("Administrator X", "admin@example.com", encodedPassword, Role.ADMIN);

            System.out.println("Initial admin created");
        } if (courierRepository.count() == 0) {
            createPerson("Courier X", "courier@example.com", encodedPassword, Role.COURIER);

            System.out.println("Initial courier crated");
        }
        if (userJpaRepository.count() == 0) {
            createPerson("User X", "user@example.com", encodedPassword, Role.USER);

            System.out.println("Initial user crated");
        }
    }

    private void createPerson(String name, String email, String encodedPass, Role role) {
        personJpaRepository.save(switch (role) {
            case ADMIN -> new AdminJpaEntity(name, email, encodedPass);
            case USER -> new UserJpaEntity(name, email, encodedPass);
            case COURIER -> new CourierJpaEntity(name, email, encodedPass);
        });
    }

}
