package gytis.courier.config;

import gytis.courier.adapter.out.persistence.person.admin.AdminJpaEntity;
import gytis.courier.adapter.out.persistence.person.courier.CourierJpaEntity;
import gytis.courier.adapter.out.persistence.person.admin.AdminJpaRepository;
import gytis.courier.adapter.out.persistence.person.courier.CourierJpaRepository;
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
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (adminRepository.count() == 0) {

            String encodedPassword = passwordEncoder.encode("pass123");

            AdminJpaEntity admin = new AdminJpaEntity();
            admin.setName("Administrator X");
            admin.setEmail("admin@example.com");
            admin.setPassword(encodedPassword);

            System.out.println("Initial admin created");

            adminRepository.save(admin);
        } else if (courierRepository.count() == 0) {
            String encodedPassword = passwordEncoder.encode("pass123");

            CourierJpaEntity courier = new CourierJpaEntity(
                    "Courier X",
                    "courier@example.com",
                    encodedPassword
            );
            System.out.println("Initial courier crated");

            courierRepository.save(courier);
        }
    }
}
