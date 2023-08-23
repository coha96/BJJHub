package com.coha.bjjhub.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
@Setter
@Getter
public class PasswordResetTokens {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String resetToken;

    @Column(nullable = false)
    private LocalDateTime tokenExpiryDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;  // 외래 키로 사용자를 참조
}
