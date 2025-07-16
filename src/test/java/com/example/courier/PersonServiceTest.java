package com.example.courier;

import com.example.courier.domain.*;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.CourierDTO;
import com.example.courier.dto.mapper.BanHistoryMapper;
import com.example.courier.dto.mapper.PersonMapper;
import com.example.courier.dto.request.PersonDetailsUpdateRequest;
import com.example.courier.dto.request.person.BanActionRequestDTO;
import com.example.courier.dto.request.person.PasswordChangeDTO;
import com.example.courier.dto.request.person.UserEditDTO;
import com.example.courier.dto.response.BanHistoryDTO;
import com.example.courier.dto.response.person.AdminPersonResponseDTO;
import com.example.courier.dto.response.person.AdminProfileResponseDTO;
import com.example.courier.dto.response.person.PersonResponseDTO;
import com.example.courier.dto.response.person.UserResponseDTO;
import com.example.courier.exception.ResourceNotFoundException;
import com.example.courier.repository.BanHistoryRepository;
import com.example.courier.repository.PersonRepository;
import com.example.courier.service.person.PersonServiceImpl;
import com.example.courier.service.person.strategy.AdminInfoStrategy;
import com.example.courier.service.person.strategy.PersonInfoStrategy;
import com.example.courier.service.person.strategy.UserInfoStrategy;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.validation.EmailValidator;
import com.example.courier.validation.PasswordValidator;
import com.example.courier.validation.PersonDetailsValidator;
import com.example.courier.validation.PhoneValidator;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @Mock private Authentication authentication;
    @Mock private SecurityContext securityContext;
    @Mock private PersonRepository personRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private PasswordValidator passwordValidator;
    @Mock private PersonMapper personMapper;
    @Mock private BanHistoryRepository banHistoryRepository;
    @Mock private BanHistoryMapper banHistoryMapper;
    @Mock private PhoneValidator phoneValidator;
    @Mock private CurrentPersonService currentPersonService;
    @Mock private UserInfoStrategy userInfoStrategy;
    @Mock private AdminInfoStrategy adminInfoStrategy;
    @Mock private PersonDetailsValidator personDetailsValidator;
    @Mock private EmailValidator emailValidator;

    private Person testPerson;

    private PersonServiceImpl personService;

    @BeforeEach
    void setup() {
        List<PersonInfoStrategy> strategyList = List.of(userInfoStrategy, adminInfoStrategy);
        personService = new PersonServiceImpl(personRepository, personMapper, banHistoryRepository, banHistoryMapper,
                phoneValidator, passwordValidator, passwordEncoder, currentPersonService, strategyList,
                personDetailsValidator, emailValidator);
    }

    @Nested
    class UpdateDetails {
        static Stream<Arguments> provideUpdateRequests() {
            return Stream.of(
                    Arguments.of(
                            new PersonDetailsUpdateRequest("updated name", "updated@email.lt",
                            "123123"),
                            createMockPerson(1L, "name", "USER")
                    ),
                    Arguments.of(
                            new PersonDetailsUpdateRequest("updated another name", "another@email.lt", "123123"),
                            createMockPerson(2L, "admin name", "ADMIN")
                    )
            );
        }

        @ParameterizedTest
        @MethodSource("provideUpdateRequests")
        @DisplayName("successfully updates details with different inputs")
        void shouldUpdateDetailsSuccessfully(PersonDetailsUpdateRequest request, Person existingPerson) {
            when(personRepository.findById(existingPerson.getId())).thenReturn(Optional.of(existingPerson));

            personService.updateDetails(existingPerson.getId(), request);

            verify(personRepository).findById(existingPerson.getId());
            verify(personRepository).save(existingPerson);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when person is not found")
        void shouldThrowWhenPersonNotFound() {
            PersonDetailsUpdateRequest request = mock(PersonDetailsUpdateRequest.class);

            when(personRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> personService.updateDetails(1L, request));

            verify(personRepository).findById(1L);
            verifyNoInteractions(personMapper);
        }
    }

    @Nested
    class Delete {
        @Test
        @DisplayName("should successfully delete person")
        void delete_shouldSuccess() {
            testPerson = createMockPerson(1L, "test", "USER");

            when(personRepository.findByIdAndIsDeletedFalse(testPerson.getId())).thenReturn(Optional.of(testPerson));

            personService.delete(testPerson.getId());

            verify(personRepository).save(testPerson);
            assertTrue(testPerson.isDeleted());
        }

        @Test
        @DisplayName("should throw IllegalStateException as person not found")
        void personNotFound_shouldThrow() {
            when(personRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () ->
                    personService.delete(1L));
        }
    }

    @ParameterizedTest
    @MethodSource("provideStatusUpdateCases")
    void testBanUnban(boolean expectedBlockedStatus, boolean initialBlockedStatus, Person person, BanActionRequestDTO requestDTO) {
        person.setBlocked(initialBlockedStatus);

        when(personRepository.findByIdAndIsDeletedFalse(person.getId())).thenReturn(Optional.of(person));
        when(currentPersonService.getCurrentPerson()).thenReturn(mock(Person.class));

        personService.banUnban(person.getId(), requestDTO);

        assertEquals(expectedBlockedStatus, person.isBlocked());
    }

    static Stream<Arguments> provideStatusUpdateCases() {
        return Stream.of(
                Arguments.of(true, false, createMockPerson(1L, "BanMe", "USER"),
                        new BanActionRequestDTO("ban reason")),
                Arguments.of(false, true, createMockPerson(2L, "UnbanMe", "ADMIN"),
                        new BanActionRequestDTO("sorry"))
        );
    }

    @ParameterizedTest(name = "{index}: Test with {0}")
    @MethodSource("provideCourierListCases")
    @DisplayName("successfully get available couriers")
    void successfullyReceiveAvailableCouriersList(List<Person> courierList, List<CourierDTO> expectedDtoList, int expectedMapperCalls) {
        when(personRepository.findAll(ArgumentMatchers.<Specification<Person>> any())).thenReturn(courierList);

        for (int i = 0; i < expectedMapperCalls; i++) {
            when(personMapper.toCourierDTO(courierList.get(i))).thenReturn(expectedDtoList.get(i));
        }

        List<CourierDTO> response = personService.getAvailableCouriers();

        assertEquals(expectedDtoList.size(), response.size());
        verify(personMapper, times(expectedMapperCalls)).toCourierDTO(any(Person.class));
    }

    static Stream<Arguments> provideCourierListCases() {
        Person courier1 = createMockPerson(1L, "name", "COURIER");
        Person courier2 = createMockPerson(2L, "name2", "COURIER");

        CourierDTO dto1 = new CourierDTO(1L, "name", "email@e.lt", false);
        CourierDTO dto2 = new CourierDTO(2L, "name2", "email2@e.lt", false);

        return Stream.of(
                Arguments.of(List.of(courier1, courier2), List.of(dto1, dto2), 2),
                Arguments.of(List.of(), List.of(), 0)
        );
    }

    @Nested
    class AvailableCouriersCount {
        @Test
        @DisplayName("receive available couriers count")
        void shouldReturnAvailableCouriersCount() {
            when(personRepository.countAvailableCouriers(any(Specification.class))).thenReturn(2L);

            Long availableCouriersCount = personService.availableCouriersCount();

            verify(personRepository).countAvailableCouriers(any());
            assertEquals(2L, availableCouriersCount);
        }
    }

    @Nested
    class GetBanHistory {
        @Test
        @DisplayName("successfully retrieve ban history")
        void successfullyReturnsBanHistory() {
            BanHistoryDTO dto = mock(BanHistoryDTO.class);
            BanHistoryDTO dto2 = mock(BanHistoryDTO.class);

            when(banHistoryRepository.findByPersonIdOrderByActionTimeDesc(1L))
                    .thenReturn(List.of(mock(BanHistory.class), mock(BanHistory.class)));
            when(banHistoryMapper.toDTO(any(BanHistory.class))).thenReturn(dto, dto2);

            var response = personService.getBanHistory(1L);

            assertNotNull(response);
            verify(banHistoryRepository).findByPersonIdOrderByActionTimeDesc(eq(1L));
            verify(banHistoryMapper, times(2)).toDTO(any(BanHistory.class));
            assertThat(response).hasSize(2).containsExactly(dto, dto2);
            assertSame(dto, response.getFirst());
            assertSame(dto2, response.get(1));
        }

        @Test
        @DisplayName("returns empty list")
        void returnsEmptyList() {
            when(banHistoryRepository.findByPersonIdOrderByActionTimeDesc(1L)).thenReturn(List.of());

            var response = personService.getBanHistory(1L);

            assertNotNull(response);
            verifyNoInteractions(banHistoryMapper);
            verify(banHistoryRepository, times(1)).findByPersonIdOrderByActionTimeDesc(eq(1L));
            assertThat(response).isEmpty();
        }
    }

    @Nested
    class MyInfo {
        @Test
        @DisplayName("should return dto for simple user")
        void shouldReturnForSimpleUser() {
            testPerson = createMockPerson(1L, "test name", "USER");

            when(currentPersonService.getCurrentPerson()).thenReturn(testPerson);
            when(userInfoStrategy.supports(testPerson)).thenReturn(true);
            when(userInfoStrategy.map(testPerson)).thenReturn(mock(UserResponseDTO.class));

            PersonResponseDTO responseDTO = personService.myInfo();

            assertNotNull(responseDTO);
            verify(userInfoStrategy, times(1)).map(testPerson);
            assertInstanceOf(UserResponseDTO.class, responseDTO);
        }

        @Test
        @DisplayName("should return for admin")
        void shouldReturnForAdmin() {
            testPerson = createMockPerson(1L, "test admin", "ADMIN");

            when(currentPersonService.getCurrentPerson()).thenReturn(testPerson);
            when(adminInfoStrategy.supports(testPerson)).thenReturn(true);
            when(adminInfoStrategy.map(testPerson)).thenReturn(mock(AdminProfileResponseDTO.class));

            PersonResponseDTO responseDTO = personService.myInfo();

            assertNotNull(responseDTO);
            verify(adminInfoStrategy, times(1)).map(testPerson);
            assertInstanceOf(AdminProfileResponseDTO.class, responseDTO);
        }

        @Test
        @DisplayName("should throw IllegalStateException when no strategy supports the person")
        void shouldThrow_personIsNotSupportedByAnyStrategy () {
            testPerson = createMockPerson(1L, "test", "COURIER");

            when(currentPersonService.getCurrentPerson()).thenReturn(testPerson);
            when(userInfoStrategy.supports(testPerson)).thenReturn(false);
            when(adminInfoStrategy.supports(testPerson)).thenReturn(false);

            assertThrows(IllegalStateException.class, () -> personService.myInfo());
            verify(userInfoStrategy, never()).map(testPerson);
            verify(adminInfoStrategy, never()).map(testPerson);
        }
    }

    @Nested
    class UpdateMyInfo {
        @Test
        @DisplayName("successfully updates information")
        void shouldSuccessfullyUpdateUserInformation() {
            User user = new User("name", "email@test.lt", "pass");
            Address address = new Address();
            address.setId(100L);
            user.setAddresses(List.of(address));

            UserEditDTO dto = new UserEditDTO(Optional.of("123456"), Optional.of(100L), Optional.of(true));

            when(currentPersonService.getCurrentPersonAs(User.class)).thenReturn(user);
            when(phoneValidator.isValid("123456")).thenReturn(true);
            when(phoneValidator.format("123456")).thenReturn("12345678");

            ApiResponseDTO responseDTO = personService.updateMyInfo(dto);

            assertEquals("success", responseDTO.status());
            assertEquals(address, user.getDefaultAddress());
            assertEquals("12345678", user.getPhoneNumber());
            assertTrue(user.isSubscribed());
            verify(personRepository).save(user);
        }

        @Test
        @DisplayName("successfully updates information")
        void phoneValidationCases() {

        }

        private User createMockUserWithAddress() {
            User user = new User("name", "email@test.lt", "pass");
            Address address = new Address();
            address.setId(100L);
            user.setAddresses(List.of(address));

            return user;
        }
    }



    @Nested
    class ChangePassword {
        @BeforeEach
        void setUp() {
            testPerson = createMockPerson(1L, "name", "USER");
            testPerson.setEmail("test@example.com");

            SecurityContextHolder.setContext(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            when(authentication.getPrincipal()).thenReturn(testPerson);
            when(authentication.isAuthenticated()).thenReturn(true);

            when(personRepository.findById(any())).thenReturn(Optional.of(testPerson));
        }

        @Test
        void changePassword_successfulUpdate() {
            PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO(
                    "newPassword1", "currentPassword1"
            );

            when(personRepository.findById(any())).thenReturn(Optional.of(testPerson));
            when(passwordEncoder.matches(passwordChangeDTO.currentPassword(), testPerson.getPassword())).thenReturn(true);
            doNothing().when(passwordValidator).validatePassword(passwordChangeDTO.newPassword());
            when(passwordEncoder.encode(passwordChangeDTO.newPassword())).thenReturn("encodedNewPassword");

            ApiResponseDTO responseDTO = personService.changePassword(passwordChangeDTO);

            assertEquals("success", responseDTO.status());
            verify(personRepository).save(argThat(p ->
                    p.getPassword().equals("encodedNewPassword") &&
                            p.getEmail().equals("test@example.com")
            ));
        }

        @Test
        void changePassword_wrongCurrentPassword_shouldFail() {
            PasswordChangeDTO dto = new PasswordChangeDTO(
                    "wrongpassword",
                    "newValidPassword1"
            );

            when(passwordEncoder.matches(dto.currentPassword(), testPerson.getPassword()))
                    .thenReturn(false);

            assertThrows(ValidationException.class, () ->
                    personService.changePassword(dto)
            );
            verify(personRepository, never()).save(any());
        }

        @Test
        void changePassword_invalidNewPassword_shouldFail() {
            PasswordChangeDTO dto = new PasswordChangeDTO(
                    "newInvalid",
                    "currentPassword1"
            );

            when(passwordEncoder.matches(dto.currentPassword(), testPerson.getPassword())).thenReturn(true);
            doThrow(new ValidationException("Invalid password")).when(passwordValidator)
                    .validatePassword(dto.newPassword());
            assertThrows(ValidationException.class, () ->
                    personService.changePassword(dto));
        }
    }

    private static Person createMockPerson(Long id, String name, String role) {
        return new Person() {
            @Override public Long getId() { return id; }
            @Override public String getName() { return name; }
            @Override public String getRole() { return role; }
        };
    }
}
