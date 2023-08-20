package com.coha.bjjhub.dto;

import lombok.Getter;

@Getter
public class UserDTO {
    private Long id;
    private String userId;
    private String name;
    private String password;
    private String phoneNumber;
    private String email;
    private String role;
    private String refreshToken;
}
