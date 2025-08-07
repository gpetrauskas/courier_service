package com.example.courier.personservice;

import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import com.example.courier.dto.CourierDTO;
import com.example.courier.dto.mapper.PersonMapper;
import com.example.courier.dto.response.person.AdminPersonResponseDTO;
import com.example.courier.repository.PersonRepository;
import com.example.courier.service.person.query.AdminPersonQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminPersonQueryServiceTest {
    @Mock private PersonRepository personRepository;
    @Mock private PersonMapper mapper;

    @InjectMocks private AdminPersonQueryService queryService;

    private final User user1 = new User("user one", "user1@email.lt", "encodedPassword");
    private final User user2 = new User("user two", "user2@email.lt", "encodedPassword");

    @BeforeEach
    void setup() {

    }

    @Test
    void findAllPaginated_shouldReturnPaginatedResults() {
        User user1 = new User("user one", "user1@email.lt", "encodedPassword");
        User user2 = new User("user two", "user2@email.lt", "encodedPassword");

        List<Person> mockPersons = List.of(user2, user1);
        Page<Person> mockPage = new PageImpl<>(mockPersons, PageRequest.of(0, 2), mockPersons.size());

        when(personRepository.findAll(ArgumentMatchers.<Specification<Person>>any(), any(Pageable.class))).thenReturn(mockPage);
        when(mapper.toAdminPersonResponseDTO(any(User.class))).thenAnswer(inv -> {
            Person p = inv.getArgument(0);
            return new AdminPersonResponseDTO(p.getName(), p.getEmail(), p.getPhoneNumber(), null,
                    1L, false, false, null);
        });

        var response = queryService.findAllPaginated(0, 2, "", "", "email", "desc");

        assertEquals(2, response.data().size());
        assertThat(response.data().getFirst().email()).isEqualTo("user2@email.lt");
        assertThat(response.data().getLast().email()).isEqualTo("user1@email.lt");
        assertThat(response.totalItems()).isEqualTo(2);
        assertThat(response.totalPages()).isEqualTo(1);
        assertThat(response.currentPage()).isEqualTo(0);
        verify(personRepository).findAll(ArgumentMatchers.<Specification<Person>>any(), any(Pageable.class));
        verify(mapper, times(2)).toAdminPersonResponseDTO(any(User.class));
    }

    @Test
    void findAllPaginated_shouldHandleEmptyResults() {
        when(personRepository.findAll(ArgumentMatchers.<Specification<Person>>any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        var response = queryService.findAllPaginated(0, 2, "", "", "email", "asc");

        assertTrue(response.data().isEmpty());
    }

    @Test
    void findAllPaginated_shouldHandleDifferentSortDirections() {
        List<Person> mockPersons = List.of(user1, user2);
        when(personRepository.findAll(ArgumentMatchers.<Specification<Person>>any(), any(Pageable.class))).thenReturn(new PageImpl<>(mockPersons));

        queryService.findAllPaginated(0, 10, "", "", "email", "asc");
        queryService.findAllPaginated(0, 10, "", "", "email", "desc");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(personRepository, times(2)).findAll(ArgumentMatchers.<Specification<Person>>any(), captor.capture());

        List<Pageable> allPageables = captor.getAllValues();
        assertThat(allPageables.get(0).getSort()).isEqualTo(Sort.by(Sort.Direction.ASC, "email"));
        assertThat(allPageables.get(1).getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "email"));
    }

    @Test
    void getAvailableCouriers_shouldReturnAvailableCouriersCount() {
        when(personRepository.findAll(ArgumentMatchers.<Specification<Person>>any())).thenReturn(List.of(user1));
        when(mapper.toCourierDTO(user1)).thenReturn(new CourierDTO(99L, "user one", "user1@email.lt", false));

        var response = queryService.getAvailableCouriers();

        assertEquals(1, response.size());
        assertThat(response.getFirst())
                .returns(99L, CourierDTO::id)
                .returns(false, CourierDTO::isBlocked);
        verify(personRepository).findAll(ArgumentMatchers.<Specification<Person>>any());
        verify(mapper).toCourierDTO(user1);
    }

    @Test
    void getAvailableCouriers_shouldHandleEmptyList() {
        when(personRepository.findAll(ArgumentMatchers.<Specification<Person>>any())).thenReturn(List.of());

        var response = queryService.getAvailableCouriers();

        assertTrue(response.isEmpty());
        verify(mapper, never()).toCourierDTO(any());
    }

    @Test
    void availableCouriersCount_shouldReturnAvailableCouriersCount() {
        when(personRepository.countAvailableCouriers(ArgumentMatchers.any())).thenReturn(20L);

        Long availableCouriersCount = queryService.availableCouriersCount();

        assertEquals(20L, availableCouriersCount);
        verify(personRepository).countAvailableCouriers(ArgumentMatchers.any());
    }
}
