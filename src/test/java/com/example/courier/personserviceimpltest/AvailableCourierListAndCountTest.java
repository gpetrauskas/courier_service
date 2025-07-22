package com.example.courier.personserviceimpltest;

import com.example.courier.domain.Person;
import com.example.courier.dto.CourierDTO;
import com.example.courier.dto.mapper.PersonMapper;
import com.example.courier.repository.PersonRepository;
import com.example.courier.service.person.PersonServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AvailableCourierListAndCountTest {

    @Mock private PersonRepository personRepository;
    @Mock private PersonMapper personMapper;

    @InjectMocks private PersonServiceImpl personService;

    private Person person;
    private CourierDTO courierDTO;

    @BeforeEach
    void setup() {
        person = mock(Person.class);
        courierDTO = mock(CourierDTO.class);
    }

    @Test
    @DisplayName("should return available couriiers mapped to dto")
    void getAvailableCouriers_shouldReturnAvailableCouriers() {
        List<Person> personList = List.of(person);

        when(personRepository.findAll(any(Specification.class))).thenReturn(personList);
        when(personMapper.toCourierDTO(person)).thenReturn(courierDTO);

        List<CourierDTO> result = personService.getAvailableCouriers();

        assertEquals(1, result.size());
        assertEquals(courierDTO, result.getFirst());
        verify(personRepository).findAll(any(Specification.class));
        verify(personMapper).toCourierDTO(any());
    }

    @Test
    @DisplayName("should return empty list if no couriers found")
    void getAvailableCouriers_shouldReturnEmptyListIfNoCouriersFound() {
        when(personRepository.findAll(any(Specification.class))).thenReturn(List.of());

        List<CourierDTO> result = personService.getAvailableCouriers();

        assertEquals(0, result.size());
        verify(personMapper, never()).toCourierDTO(any());
    }

    @Test
    @DisplayName("should return available couriers count")
    void availableCouriersCount_shouldReturnCount() {
        when(personRepository.countAvailableCouriers(any(Specification.class))).thenReturn(2L);

        Long couriersCount = personService.availableCouriersCount();

        assertEquals(2, couriersCount);
        verify(personRepository).countAvailableCouriers(any(Specification.class));
    }
}
