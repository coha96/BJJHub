package com.coha.bjjhub.service;

import com.coha.bjjhub.entity.PasswordResetTokens;
import com.coha.bjjhub.entity.User;
import com.coha.bjjhub.entity.UserRefreshTokens;
import com.coha.bjjhub.exception.InvalidCredentialsException;
import com.coha.bjjhub.exception.UserNotFoundException;
import com.coha.bjjhub.repository.PasswordResetTokenRepository;
import com.coha.bjjhub.repository.RefreshTokenRepository;
import com.coha.bjjhub.repository.UserRepository;
import com.coha.bjjhub.exception.UserAlreadyExistsException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public User register(User user) {
        if (userRepository.findByUserId(user.getUserId()).isPresent()) {
            throw new UserAlreadyExistsException("이미 등록된 아이디입니다.");
        }
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
        UserRefreshTokens userRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken).orElse(null);
        return userRefreshToken != null ? userRefreshToken.getUser() : null;
    }

    public void createPasswordResetTokenForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));

        String resetToken = UUID.randomUUID().toString();
        PasswordResetTokens passwordResetToken = new PasswordResetTokens();
        passwordResetToken.setUser(user);
        passwordResetToken.setResetToken(resetToken);
        passwordResetToken.setTokenExpiryDate(LocalDateTime.now().plusHours(1));
        passwordResetTokenRepository.save(passwordResetToken);

        String resetLink = "https://yourdomain.com/reset-password?token=" + resetToken;
        emailService.sendSimpleMessage(email, "비밀번호 재설정", "비밀번호를 재설정하려면 다음 링크를 클릭하세요: " + resetLink);
    }

    public void validateAndResetPassword(String token, String newPassword) {
        PasswordResetTokens passwordResetToken = passwordResetTokenRepository.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 또는 만료된 재설정 토큰입니다."));

        if (passwordResetToken.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("재설정 토큰이 만료되었습니다.");
        }

        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(passwordResetToken);
    }

    public void saveRefreshToken(UserRefreshTokens userRefreshToken) {
        refreshTokenRepository.save(userRefreshToken);
    }
}