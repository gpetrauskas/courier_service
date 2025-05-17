package com.example.courier;

import com.example.courier.config.JwtAuthenticationFilter;
import com.example.courier.config.SecurityConfig;
import com.example.courier.controller.RegistrationController;
import com.example.courier.service.RegistrationService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrationController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RegistrationService registrationService;
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean
    private SecurityConfig securityConfig;

    private final String userRegUrl = "/api/registration/register";
    private final String courierRegUrl = "/api/registration/registerCourier";
    private final String validJson = """
            {
                "name": "Test courier",
                "email": "courier@example.com",
                "password": "ValidPass123"
            }
            """;


    @Nested
    class UserRegistrationTests {

        @Test
        @WithMockUser
        void registerUser_withValidData_returns200() throws Exception{
            mockMvc.perform(post(userRegUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validJson))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        void registerUser_withInvalidData_returns400() throws Exception {
            mockMvc.perform(post(userRegUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{invalidData}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("error"));
        }
    }

    @Nested
    class CourierRegistrationTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        void registerCourier_asAdmin_returns200() throws Exception {
            mockMvc.perform(post(courierRegUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validJson))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        void registerCourier_asUser_returns403() throws Exception {
            mockMvc.perform(post(courierRegUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validJson))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void registerCourier_withInvalidData_returns400() throws Exception {
            mockMvc.perform(post(courierRegUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{invalidData}"))
                    .andExpect(status().isBadRequest());
        }
    }
}
