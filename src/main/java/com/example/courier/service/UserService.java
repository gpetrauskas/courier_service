package com.example.courier.service;

import com.example.courier.dto.UserDTO;
import com.example.courier.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void registerUser(UserDTO userDTO) {
        try {
            validateUserRegistration(userDTO);
        } catch (Exception e) {

        }
    }

    private void validateUserRegistration(UserDTO userDTO) {

    }



}
