package com.example.courier.personserviceimpltest;

import com.example.courier.domain.Admin;
import com.example.courier.domain.Person;
import com.example.courier.dto.mapper.PersonMapper;
import com.example.courier.dto.response.person.AdminProfileResponseDTO;
import com.example.courier.dto.response.person.UserResponseDTO;
import com.example.courier.repository.PersonRepository;
import com.example.courier.service.person.PersonServiceImpl;
import com.example.courier.service.person.strategy.AdminInfoStrategy;
import com.example.courier.service.person.strategy.PersonInfoStrategy;
import com.example.courier.service.person.strategy.UserInfoStrategy;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.service.transformation.PersonTransformationService;
import com.example.courier.service.validation.PersonValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MyInfoTest {

    @Mock private CurrentPersonService currentPersonService;
    @Mock private PersonRepository personRepository;
    @Mock private PersonMapper personMapper;
    @Mock private UserInfoStrategy userInfoStrategy;
    @Mock private AdminInfoStrategy adminInfoStrategy;
    @Mock private List<PersonInfoStrategy> strategies;
    @Mock private PersonValidationService personValidationService;
    @Mock private PersonTransformationService personTransformationService;

    private PersonServiceImpl personService;

    private Person person;

    @BeforeEach
    void setup() {
        strategies = List.of(adminInfoStrategy, userInfoStrategy);
        personService = new PersonServiceImpl(personRepository, personMapper, null, null,
                null, currentPersonService, strategies,
                null, personValidationService, personTransformationService);

        person = new Person() {
            @Override public String getRole() { return "USER"; }
            @Override public Long getId() { return 1L; }
        };
    }

    @Test
    @DisplayName("should return user response when UserInfoStrategy supports")
    void shouldReturnUserResponse() {
        when(currentPersonService.getCurrentPerson()).thenReturn(person);
        when(userInfoStrategy.supports(person)).thenReturn(true);
        when(userInfoStrategy.map(person)).thenReturn(mock(UserResponseDTO.class));

        var response = personService.myInfo();

        assertNotNull(response);
        assertInstanceOf(UserResponseDTO.class, response);
    }

    @Test
    @DisplayName("should return admin response when AdminInfoStrategy supports")
    void shouldReturnAdminProfileResponse() {
        Person person1 = new Admin();

        when(currentPersonService.getCurrentPerson()).thenReturn(person1);
        when(adminInfoStrategy.supports(person1)).thenReturn(true);
        when(adminInfoStrategy.map(person1)).thenReturn(mock(AdminProfileResponseDTO.class));

        var response = personService.myInfo();

        assertNotNull(response);
        assertInstanceOf(AdminProfileResponseDTO.class, response);
    }

    @Test
    @DisplayName("should throw when no strategy supports person")
    void shouldThrowForUnsupportedPerson() {
        when(currentPersonService.getCurrentPerson()).thenReturn(person);
        when(userInfoStrategy.supports(person)).thenReturn(false);
        when(adminInfoStrategy.supports(person)).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> personService.myInfo());
    }
}
