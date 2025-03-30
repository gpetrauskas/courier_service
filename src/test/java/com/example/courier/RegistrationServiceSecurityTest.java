package com.example.courier;

import com.example.courier.dto.RegistrationDTO;
import com.example.courier.service.RegistrationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class RegistrationServiceSecurityTest {

    private final static RegistrationDTO validReg =
            new RegistrationDTO("Test Courier", "test_" + System.currentTimeMillis() + "@test.com",  "goodPassword123");

    @Autowired
    private RegistrationService registrationService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin can register courier")
    void registerCourier_allowedForAdmin() {
        assertDoesNotThrow(() -> registrationService.registerCourier(validReg));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("User fail register admin")
    void registerCourier_notAllowedForUser() {
        assertThrows(AccessDeniedException.class,
                () -> registrationService.registerCourier(validReg));
    }
}
