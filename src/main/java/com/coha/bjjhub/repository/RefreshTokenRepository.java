package com.coha.bjjhub.repository;

import com.coha.bjjhub.entity.UserRefreshTokens;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<UserRefreshTokens, Long> {
    Optional<UserRefreshTokens> findByRefreshToken(String refreshToken);
}