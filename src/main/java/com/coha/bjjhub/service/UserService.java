package com.coha.bjjhub.service;

import com.coha.bjjhub.entity.User;
import com.coha.bjjhub.exception.InvalidCredentialsException;
import com.coha.bjjhub.exception.UserNotFoundException;
import com.coha.bjjhub.repository.UserRepository;
import com.coha.bjjhub.exception.UserAlreadyExistsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
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

    public void createPasswordResetTokenForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setTokenExpiryDate(LocalDateTime.now().plusHours(1)); // 토큰은 1시간 동안 유효

        // 이메일 전송 로직
        String resetLink = "https://yourdomain.com/reset-password?token=" + resetToken;
        // 실제 프론트엔드와 협업을 진행한다면 프론트엔드 웹 페이지의 링크가 될 것이다.
        // 이 웹 페이지는 사용자가 새로운 비밀번호를 입력할 수 있는 화면을 제공하고,
        // 사용자가 새 비밀번호를 입력하면 해당 웹 페이지는 백엔드 API (/password-reset/confirm)에
        // 토큰과 새로운 비밀번호를 전송하여 비밀번호를 재설정하게 됩니다.
        // 즉 테스트 상황임!
        emailService.sendSimpleMessage(email, "비밀번호 재설정", "비밀번호를 재설정하려면 다음 링크를 클릭하세요: " + resetLink);

        userRepository.save(user);
    }

    public void validateAndResetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 또는 만료된 재설정 토큰입니다."));
        if (user.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("재설정 토큰이 만료되었습니다.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null); // 토큰 초기화
        user.setTokenExpiryDate(null); // 만료 날짜 초기화
        userRepository.save(user);
    }
}