package com.example.courier.service;

import com.example.courier.domain.User;
import com.example.courier.dto.UserDTO;
import com.example.courier.dto.UserResponseDTO;
import com.example.courier.exception.UserNotFoundException;
import com.example.courier.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    public List<UserResponseDTO> findAllUsers() {
        List<User> allUsers = userRepository.findAll();
        List<UserResponseDTO> allUserResponseDTOs = allUsers.stream()
                .map(UserResponseDTO::fromUser)
                .collect(Collectors.toList());
        return allUserResponseDTOs;
    }

    public Optional<UserResponseDTO> findUserById(Long id) {
        try {
            User user = userRepository.findById(id).orElseThrow(() ->
                    new UserNotFoundException("User was not found"));
            UserResponseDTO userResponseDTO = UserResponseDTO.fromUser(user);

            return Optional.of(userResponseDTO);
        } catch (UserNotFoundException e) {
            logger.warn("User not found", e.getMessage());
            return Optional.empty();
        } catch (RuntimeException e) {
            logger.error("Error occurred finding user", e);
            throw e;
        }
    }

    public void updateUser(Long id, UserDTO updatedUser) {
        try {
            User existingUser = userRepository.findById(id).orElseThrow(() ->
                    new UserNotFoundException("User was not found."));
            logger.info("AdminService: updateUser after findById");

            updateUserFields(existingUser, updatedUser);
            userRepository.save(existingUser);
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException("User was not found");
        }
    }

    private void updateUserFields(User existingUse, UserDTO updatedUser) {
        if (updatedUser.name() != null) {
            existingUse.setName(updatedUser.name());
        }
        if (updatedUser.email() != null) {
            existingUse.setEmail(updatedUser.email());
        }
        if (updatedUser.address() != null) {
            existingUse.setAddress(updatedUser.address());
        }
        if (updatedUser.password() != null) {
            String encodedPass = passwordEncoder.encode(updatedUser.password());
            existingUse.setPassword(encodedPass);
        }
    }

    public void deleteUser(Long id) {
            User user = userRepository.findById(id).orElseThrow(() ->
                    new UserNotFoundException("User was not found."));
            logger.info("User was found for deletion");
            userRepository.delete(user);
    }
}
