package com.example.courier.personserviceimpltest;

import com.example.courier.domain.Admin;
import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import com.example.courier.dto.mapper.PersonMapper;
import com.example.courier.dto.response.person.AdminPersonResponseDTO;
import com.example.courier.repository.PersonRepository;
import com.example.courier.service.person.PersonServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FindAllPaginatedTest {
    @Mock
    private PersonRepository personRepository;
    @Mock
    private PersonMapper personMapper;

    @InjectMocks
    private PersonServiceImpl personService;

    @ParameterizedTest(name = "should return {1} person(s)")
    @MethodSource("provideFindAllCases")
    @DisplayName("should return expected number of mapped persons")
    void shouldReturnExpectedNumberOfPersons(List<Person> personList, int expectedSize) {
        when(personRepository.findAll(ArgumentMatchers.<Specification<Person>> any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(personList));

        personList.forEach(person -> when(personMapper.toAdminPersonResponseDTO(person))
                .thenReturn(mock(AdminPersonResponseDTO.class)));

        var response = personService.findAllPaginated(0, 10, "", "", "id", "asc");

        assertNotNull(response);
        assertEquals(expectedSize, response.data().size());
        response.data().forEach(Assertions::assertNotNull);
    }

    static Stream<Arguments> provideFindAllCases() {
        return Stream.of(
                Arguments.of(List.of(new User(), new Admin()), 2),
                Arguments.of(List.of(), 0)
        );
    }
}
