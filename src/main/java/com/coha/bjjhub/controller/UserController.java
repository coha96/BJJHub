package com.coha.bjjhub.controller;

import com.coha.bjjhub.dto.RefreshTokenDTO;
import com.coha.bjjhub.dto.UserDTO;
import com.coha.bjjhub.dto.UserLoginDTO;
import com.coha.bjjhub.entity.RoleType;
import com.coha.bjjhub.entity.User;
import com.coha.bjjhub.entity.UserRefreshTokens;
import com.coha.bjjhub.service.UserService;
import com.coha.bjjhub.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO userDto) {
        User user = convertToEntity(userDto);
        userService.register(user);
        return ResponseEntity.ok("회원 가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserLoginDTO userLoginDTO) {
        User user = userService.login(userLoginDTO.getUserId(), userLoginDTO.getPassword());
        String token = jwtUtil.generateToken(user.getUserId());
        String refreshToken = jwtUtil.generateRefreshToken();

        UserRefreshTokens userRefreshToken = new UserRefreshTokens();
        userRefreshToken.setRefreshToken(refreshToken);
        userRefreshToken.setUser(user);
        userService.saveRefreshToken(userRefreshToken);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("jwt", token);
        tokens.put("refreshToken", refreshToken);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        String refreshToken = refreshTokenDTO.getRefreshToken();

        // 만료된 리프레시 토큰 체크
        if (jwtUtil.isRefreshTokenExpired(refreshToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("토큰이 만료되었습니다.");
        }

        // 데이터베이스에서 리프레시 토큰과 일치하는 사용자를 찾는다.
        User user = userService.findByRefreshToken(refreshToken);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("잘못된 토큰을 입력하였습니다.");
        }

        // 새로운 토큰 생성
        String newToken = jwtUtil.generateToken(user.getUserId());

        // 새로운 토큰 반환
        return ResponseEntity.ok(newToken);
    }

    private User convertToEntity(UserDTO userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setUserId(userDto.getUserId());
        user.setName(userDto.getName());
        user.setPassword(userDto.getPassword());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setEmail(userDto.getEmail());
        user.setRole(RoleType.valueOf(userDto.getRole()));  // String에서 Enum으로 변환

        return user;
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
        userService.createPasswordResetTokenForUser(email);
        return ResponseEntity.ok("재설정 토큰을 이메일로 전송하였습니다.");
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<String> confirmPasswordReset(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");
        userService.validateAndResetPassword(token, newPassword);
        return ResponseEntity.ok("비밀번호가 성공적으로 재설정되었습니다.");
    }
}