package com.coha.bjjhub.repository;

import com.coha.bjjhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId);
    Optional<User> findByEmail(String email);
//    Optional<User> findByResetToken(String resetToken);
//    Optional<User> findByRefreshToken(String refreshToken);

}