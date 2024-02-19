package com.example.courier;

import com.example.courier.domain.User;
import com.example.courier.dto.UserDTO;
import com.example.courier.repository.UserRepository;
import com.example.courier.service.UserService;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser_WithValidData_ShouldSaveUserToRepository() {
        // arrange, create valid user dto
        UserDTO userDTO = new UserDTO("Samwise Gamgee", "samwise@middleearth.com", "Bagshot Row", "samthegardener");

        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("EncodedPassword");

        userService.registerUser(userDTO);

        // assert
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegisterUser_WithExistingEmail_ShouldThrowException() {
        // arrange
        UserDTO userDTO = new UserDTO("Samwise Gamgee", "samwise@middleearth.com", "Bagshot Row", "samthegardener");
        // mock the userRepository to return user when findByEmail is executed
        when(userRepository.findByEmail(userDTO.email())).thenReturn(new User());
        // assert
        Exception exception = assertThrows(RuntimeException.class, () -> userService.registerUser(userDTO));
        assertEquals("User already exist.", exception.getMessage());
    }

    @Test
    public void testRegister_WithInvalidPasswordLength_ShouldThrowException() {
        // arrange
        UserDTO userDTO = new UserDTO("Samwise Gamgee", "samwise@middleearth.com", "Bagshot Row", "short");

        // act and assert
        Exception exception = assertThrows(ValidationException.class, () -> userService.registerUser(userDTO));
        assertEquals("Password length must be between 8-16 characters.", exception.getMessage());
    }
}
