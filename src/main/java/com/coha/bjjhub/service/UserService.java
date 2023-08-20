package com.coha.bjjhub.service;

import com.coha.bjjhub.entity.User;
import com.coha.bjjhub.exception.InvalidCredentialsException;
import com.coha.bjjhub.exception.UserNotFoundException;
import com.coha.bjjhub.repository.UserRepository;
import com.coha.bjjhub.exception.UserAlreadyExistsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User user) {
        // 중복 ID 체크
        if (userRepository.findByUserId(user.getUserId()).isPresent()) {
            throw new UserAlreadyExistsException("이미 등록된 아이디입니다.");
        }

        // 비밀번호 암호화
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public User login(String userId, String password) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("비밀번호가 틀립니다.");
        }

        return user;
    }

    public User findByRefreshToken(String refreshToken) {
        return userRepository.findByRefreshToken(refreshToken).orElse(null);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }
}