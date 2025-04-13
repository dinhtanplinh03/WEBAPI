package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername()) || userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Username or Email already exists!");
        }
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setFullName(userDetails.getFullName());
            user.setPhone(userDetails.getPhone());
            user.setAddress(userDetails.getAddress());
            user.setRole(userDetails.getRole());
            user.setPassword(userDetails.getPassword());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User blockUser(Long id, Integer status) {
        return userRepository.findById(id).map(user -> {
            user.setBlocked(status);
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
