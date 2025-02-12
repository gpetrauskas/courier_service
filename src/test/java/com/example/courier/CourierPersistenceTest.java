package com.example.courier;

import com.example.courier.domain.Courier;
import com.example.courier.domain.Person;
import com.example.courier.repository.CourierRepository;
import com.example.courier.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class CourierPersistenceTest {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private CourierRepository courierRepository;

    @Test
    public void testCourierPersistence() {
        // Create and save a Courier
        Courier courier = new Courier();
        courier.setName("Courier Test");
        courier.setEmail("courier@test.com");
        courier.setPassword("password");
        courier.setHasActiveTask(false);

        personRepository.save(courier);

        // Fetch from both the persons and couriers tables
        Optional<Person> fetchedPerson = personRepository.findById(courier.getId());
        Optional<Courier> fetchedCourier = courierRepository.findById(courier.getId());

        // Assertions
        assertTrue(fetchedPerson.isPresent(), "Courier should be saved in the persons table");
        assertTrue(fetchedCourier.isPresent(), "Courier should be saved in the couriers table");
    }
}
