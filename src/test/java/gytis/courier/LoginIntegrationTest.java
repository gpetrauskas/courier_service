package gytis.courier;

import gytis.courier.adapter.out.security.PasswordEncoderAdapter;
import gytis.courier.application.port.out.person.UserCommandPort;
import gytis.courier.domain.person.Email;
import gytis.courier.domain.person.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoginIntegrationTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserCommandPort userCommandPort;
    @Autowired
    PasswordEncoderAdapter passwordEncoderAdapter;
    ExecutorService executorService;

    @Test
    void loginReturnsTokensAsCookies() throws Exception {
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@example.com\",\"password\":\"pass123\"}"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("jwt"))
                .andExpect(cookie().exists("refresh"));
    }

    @Test
    void loginThrowsOnPasswordDoNotMatch() throws Exception {
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@example.com\",\"password\":\"wrongPass\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void successfullyRefreshTokens() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@example.com\",\"password\":\"pass123\"}"))
                        .andReturn();

        mockMvc.perform(post("/api/auth/refresh")
                .cookie(loginResult.getResponse().getCookies()))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refresh"));
    }

    @Test
    void loginThrowsOnUsedRefreshToken() throws Exception {
        MvcResult loginResult1 = mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@example.com\",\"password\":\"pass123\"}"))
                .andReturn();

        mockMvc.perform(post("/api/auth/refresh")
                .cookie(loginResult1.getResponse().getCookies()))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refresh"));

        mockMvc.perform(post("/api/auth/refresh")
                .cookie(loginResult1.getResponse().getCookies()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void throwsOnPersonIsBlocked() throws Exception {
        String encryptedPass = passwordEncoderAdapter.encode("pass123");
        User bannedPerson = new User(99L, "name", new Email("banned@example.com"), encryptedPass);
        bannedPerson.banUnban();

        userCommandPort.create(bannedPerson);

        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"banned@example.com\",\"password\":\"pass123\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refreshThrowsWhenNoRefreshCookieIsPresent() throws Exception {
        mockMvc.perform(post("/api/auth/refresh"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void throwsOnConcurrentRefreshCookieCall() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@example.com\",\"password\":\"pass123\"}"))
                .andReturn();

        executorService = Executors.newFixedThreadPool(2);

        List<Integer> statuses = Collections.synchronizedList(new ArrayList<>());
            for (int i = 0; i < 2; i++) {
                executorService.execute(
                        () -> {
                            try {
                                MvcResult result = mockMvc.perform(post("/api/auth/refresh")
                                                .cookie(loginResult.getResponse().getCookie("refresh")))
                                        .andReturn();
                                statuses.add(result.getResponse().getStatus());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
            }

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        assertEquals(2, statuses.size());
        assertTrue(statuses.contains(200));
        assertTrue(statuses.contains(401));
    }
}
