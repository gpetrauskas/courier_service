package com.example.courier.personservice;

import com.example.courier.domain.Address;
import com.example.courier.domain.User;
import com.example.courier.dto.ApiResponseDTO;
import com.example.courier.dto.request.person.UserEditDTO;
import com.example.courier.repository.PersonRepository;
import com.example.courier.service.person.command.PersonUpdateService;
import com.example.courier.service.person.query.PersonLookupService;
import com.example.courier.service.security.CurrentPersonService;
import com.example.courier.service.transformation.PersonTransformationService;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonUpdateServiceTest {

    @Mock private PersonRepository personRepository;
    @Mock private PersonLookupService lookupService;
    @Mock private CurrentPersonService currentPersonService;
    @Mock private PersonTransformationService transformationService;

    @InjectMocks private PersonUpdateService personUpdateService;

    private static final Long TEST_PERSON_ID = 1L;
    private User testUser;

    @BeforeEach
    void setup() {
        Address defaultInitialAddress = new Address();
        defaultInitialAddress.setId(98L);

        testUser = new User();
        testUser.setName("Old Name");
        testUser.setEmail("old@email.lt");
        testUser.setPhoneNumber("37012345678");
        testUser.setSubscribed(false);
        testUser.setDefaultAddress(defaultInitialAddress);

        Address newDefaultAddress = new Address();
        newDefaultAddress.setId(99L);

        newDefaultAddress.setUser(testUser);
        testUser.setAddresses(List.of(newDefaultAddress, defaultInitialAddress));
    }

    @Test
    void updateMyInfo_shouldReturnSuccessResponseWhenAllFieldsValid() {
        UserEditDTO dto = new UserEditDTO("61111111", 99L, true);

        when(currentPersonService.getCurrentPersonId()).thenReturn(TEST_PERSON_ID);
        when(lookupService.findUserByIdWithAddresses(TEST_PERSON_ID)).thenReturn(testUser);
        when(transformationService.validateAndFormatPhone("61111111")).thenReturn("37061111111");

        var response = personUpdateService.updateMyInfo(dto);

        assertThat(response)
                .returns("success", ApiResponseDTO::status)
                .returns("Successfully updated", ApiResponseDTO::message);
        assertThat(testUser)
                .returns("37061111111", User::getPhoneNumber)
                .returns(99L, u -> u.getDefaultAddress().getId())
                .returns(true, User::isSubscribed);
    }

    @Test
    void updateMyInfo_shouldUpdateValidFieldsAndReturnSuccessResponse() {
        UserEditDTO dto = new UserEditDTO("64111111", null, true);

        when(currentPersonService.getCurrentPersonId()).thenReturn(TEST_PERSON_ID);
        when(lookupService.findUserByIdWithAddresses(TEST_PERSON_ID)).thenReturn(testUser);
        when(transformationService.validateAndFormatPhone("64111111")).thenReturn("37064111111");

        var response = personUpdateService.updateMyInfo(dto);

        assertThat(response)
                .returns("success", ApiResponseDTO::status)
                .returns("Successfully updated", ApiResponseDTO::message);
        assertThat(testUser)
                .returns("37064111111", User::getPhoneNumber)
                .returns(98L, address -> address.getDefaultAddress().getId())
                .returns(true, User::isSubscribed);
    }

    @Test
    void updateMyInfo_shouldThrowOnWrongPhoneFormat() {
        UserEditDTO dto = new UserEditDTO("124", null, null);

        when(currentPersonService.getCurrentPersonId()).thenReturn(TEST_PERSON_ID);
        when(lookupService.findUserByIdWithAddresses(TEST_PERSON_ID)).thenReturn(testUser);
        when(transformationService.validateAndFormatPhone("124")).thenThrow(new ValidationException("Phone must be 8 digits"));

        ValidationException response = assertThrows(ValidationException.class,
                () ->personUpdateService.updateMyInfo(dto));

        assertThat(response)
                .returns("Phone must be 8 digits", ValidationException::getMessage);
        assertThat(testUser)
                .returns("37012345678", User::getPhoneNumber);
        verify(personRepository, never()).save(testUser);
    }

    @Test
    void updateMyInfo_shouldIgnoreSettingDefaultAddressWhenNotOwningItInAddressList() {
        UserEditDTO dto = new UserEditDTO(null, 100L, null);

        when(currentPersonService.getCurrentPersonId()).thenReturn(TEST_PERSON_ID);
        when(lookupService.findUserByIdWithAddresses(TEST_PERSON_ID)).thenReturn(testUser);

        var response = personUpdateService.updateMyInfo(dto);

        assertThat(testUser)
                .returns(98L, address -> address.getDefaultAddress().getId());
        assertThat(response)
                .returns("success", ApiResponseDTO::status);
    }
}
