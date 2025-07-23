package com.example.courier.personserviceimpltest;

import com.example.courier.domain.Address;
import com.example.courier.domain.User;
import com.example.courier.dto.request.person.UserEditDTO;
import com.example.courier.repository.PersonRepository;
import com.example.courier.service.person.PersonServiceImpl;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.service.transformation.PersonTransformationService;
import com.example.courier.service.validation.PersonValidationService;
import com.example.courier.validation.person.PersonDetailsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateMyInfoTest {

    @Mock private CurrentPersonService currentPersonService;
    @Mock private PersonRepository personRepository;
    @Mock private PersonDetailsValidator validator;
    @Mock private PersonValidationService personValidationService;
    @Mock private PersonTransformationService personTransformationService;

    @InjectMocks private PersonServiceImpl personService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User("user name", "user@email.lt", "testPass123");
        user.setSubscribed(false);
    }

    @Test
    @DisplayName("successfully update user data")
    void shouldSuccessfullyUpdateUserinfo() {
        UserEditDTO request = new UserEditDTO("12345678", null, null);

        when(currentPersonService.getCurrentPersonAs(User.class)).thenReturn(user);
        when(personValidationService.isPhoneValid(request.phoneNumber())).thenReturn(true);
        when(personTransformationService.formatPhone(request.phoneNumber())).thenReturn("37012345678");

        var response = personService.updateMyInfo(request);

        assertEquals("37012345678", user.getPhoneNumber());
        assertEquals("success", response.status());
    }

    @Test
    @DisplayName("should update default address if exists")
    void shouldUpdateDefaultAddressIfExists() {
        UserEditDTO request = new UserEditDTO(null, 1L, null);
        Address address = new Address();
        address.setId(1L);
        user.setAddresses(List.of(address));

        when(currentPersonService.getCurrentPersonAs(User.class)).thenReturn(user);

        var response = personService.updateMyInfo(request);

        assertEquals(request.defaultAddressId(), user.getDefaultAddress().getId());
        assertEquals("success", response.status());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldSuccessfullySetSubscribedStatus(boolean newValue) {
        user.setSubscribed(!newValue);
        UserEditDTO request = new UserEditDTO(null, null, newValue);

        when(currentPersonService.getCurrentPersonAs(User.class)).thenReturn(user);

        var response = personService.updateMyInfo(request);

        assertEquals(newValue, user.isSubscribed());
        assertEquals("success", response.status());
    }
}
