package com.urlShortener.urlShortener.service;

import com.urlShortener.urlShortener.entities.User;
import com.urlShortener.urlShortener.models.CreateUserCmd;
import com.urlShortener.urlShortener.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalTime;

@Service
@Transactional(readOnly = true)
public class UserService {

    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUser(CreateUserCmd createUserCmd) {
        if(userRepository.existsUserByEmail(createUserCmd.email()))
        {
            throw new IllegalArgumentException("User with email "+createUserCmd.email()+" already exists.");
        }
        User user = new User();
        user.setName(createUserCmd.name());
        user.setEmail(createUserCmd.email());
        user.setPassword(passwordEncoder.encode(createUserCmd.password()));
        user.setRole(createUserCmd.role());
        user.setCreatedAt(Instant.now());
        return userRepository.save(user);
    }
}
