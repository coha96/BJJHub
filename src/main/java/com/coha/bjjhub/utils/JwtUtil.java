package com.coha.bjjhub.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${JWT_SECRET_KEY}")
    private String secretKey;
    @Value("${jwt.expiration.time}")
    private long EXPIRATION_TIME;

    public String generateToken(String userId) {
        return JWT.create()
                .withSubject(userId)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secretKey));
    }

    public String getUserIdFromToken(String token) {
        return JWT.require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(token)
                .getSubject();
    }

    public String generateRefreshToken() {
        return JWT.create().withSubject(UUID.randomUUID().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME * 2)) // 리프레시 토큰의 만료시간은 1시간으로 설정
                .sign(Algorithm.HMAC512(secretKey));
    }

    // 리프레시 토큰의 만료 시간을 확인하는 메서드
    public boolean isRefreshTokenExpired(String token) {
        Date expirationDate = JWT.require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(token)
                .getExpiresAt();
        return expirationDate.before(new Date()); // 현재 시간보다 만료 시간이 이전이면 true
    }
}