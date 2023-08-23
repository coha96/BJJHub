package com.coha.bjjhub.service;

import com.coha.bjjhub.config.CustomUserDetails;
import com.coha.bjjhub.entity.User;
import com.coha.bjjhub.exception.UserNotFoundException;
import com.coha.bjjhub.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));
        return new CustomUserDetails(user);
    }
}