package com.urlShortener.urlShortener.service;

import com.urlShortener.urlShortener.entities.User;
import com.urlShortener.urlShortener.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityUtils {

    private UserRepository userRepository;
    public SecurityUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null && authentication.isAuthenticated())
        {
            String username = authentication.getName();
            return userRepository.findByEmail(username).orElse(null);
        }
        return null;
    }

    public Long getUserId() {
        User user = getUser();
        return user != null ? user.getId() : null;
    }
}
