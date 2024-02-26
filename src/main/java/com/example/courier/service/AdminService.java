package com.example.courier.service;

import com.example.courier.domain.User;
import com.example.courier.dto.UserResponseDTO;
import com.example.courier.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    public List<UserResponseDTO> findAllUsers() {
        List<User> allUsers = userRepository.findAll();
        List<UserResponseDTO> allUserResponseDTOs = allUsers.stream()
                .map(UserResponseDTO::fromUser)
                .collect(Collectors.toList());
        return allUserResponseDTOs;
    }

}
