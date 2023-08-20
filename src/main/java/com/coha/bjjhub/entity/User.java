package com.coha.bjjhub.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString(exclude = "password") // 비밀번호 필드 제외
@EqualsAndHashCode
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phoneNumber;

    @Column
    private String email;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Column
    private String refreshToken;

}