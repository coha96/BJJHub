package com.coha.bjjhub.repository;

import com.coha.bjjhub.entity.PasswordResetTokens;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokens, Long> {
    Optional<PasswordResetTokens> findByResetToken(String resetToken);
}
