package com.example.userservice.service;

import com.example.userservice.dto.UserRequestDTO;
import com.example.userservice.dto.UserResponseDTO;
import com.example.userservice.dto.UserUpdateDTO;
import com.example.userservice.entity.User;
import com.example.userservice.exception.DuplicateEmailException;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.talonone.TalonOneClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final TalonOneClient talonOneClient;

    @Autowired
    public UserService(UserRepository userRepository, TalonOneClient talonOneClient) {
        this.userRepository = userRepository;
        this.talonOneClient = talonOneClient;
    }

    @Transactional
    public UserResponseDTO registerUser(UserRequestDTO requestDTO) {
        if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email already exists");
        }
        User user = new User();
        user.setName(requestDTO.getName());
        user.setEmail(requestDTO.getEmail());
        user.setPhone(requestDTO.getPhone());
        user.setTotalOrders(requestDTO.getTotalOrders());
        user.setTotalSpent(requestDTO.getTotalSpent());
        User savedUser = userRepository.save(user);
        // Talon.One integration
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", savedUser.getName());
        attributes.put("email", savedUser.getEmail());
        attributes.put("phone", savedUser.getPhone());
        attributes.put("totalOrders", savedUser.getTotalOrders());
        attributes.put("totalSpent", savedUser.getTotalSpent());
        talonOneClient.registerUser(savedUser.getEmail(), attributes);
        return mapToResponseDTO(savedUser);
    }

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return mapToResponseDTO(user);
    }

    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return mapToResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UserUpdateDTO updateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        if (updateDTO.getName() != null) user.setName(updateDTO.getName());
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(updateDTO.getEmail()).isPresent()) {
                throw new DuplicateEmailException("Email already exists");
            }
            user.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getPhone() != null) user.setPhone(updateDTO.getPhone());
        if (updateDTO.getTotalOrders() != null) user.setTotalOrders(updateDTO.getTotalOrders());
        if (updateDTO.getTotalSpent() != null) user.setTotalSpent(updateDTO.getTotalSpent());
        User savedUser = userRepository.save(user);
        // Optionally update Talon.One profile
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", savedUser.getName());
        attributes.put("email", savedUser.getEmail());
        attributes.put("phone", savedUser.getPhone());
        attributes.put("totalOrders", savedUser.getTotalOrders());
        attributes.put("totalSpent", savedUser.getTotalSpent());
        talonOneClient.registerUser(savedUser.getEmail(), attributes);
        return mapToResponseDTO(savedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
        // Optionally: delete from Talon.One if API supports it
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getTotalOrders(),
                user.getTotalSpent()
        );
    }
}
