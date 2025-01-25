package com.example.courier.config;

import com.example.courier.domain.Admin;
import com.example.courier.domain.Courier;
import com.example.courier.repository.AdminRepository;
import com.example.courier.repository.CourierRepository;
import com.example.courier.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private CourierRepository courierRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setName("Gytis Petrauskas");
            admin.setEmail("admin@email.lt");
            String encodedPassword = passwordEncoder.encode("123123123123");
            admin.setPassword(encodedPassword);
            System.out.println("Initial admin created");

            adminRepository.save(admin);
        } else if (courierRepository.count() == 2) {
            Courier courier = new Courier();
            courier.setName("Kurjeris Test2");
            courier.setEmail("kurjeris2@test.k");
            String encodedPassword = passwordEncoder.encode("123123123123");
            courier.setPassword(encodedPassword);
         //   courier.setRole(Role.COURIER);
            System.out.println("Initial courier crated");

            courierRepository.save(courier);
        }
    }

}
