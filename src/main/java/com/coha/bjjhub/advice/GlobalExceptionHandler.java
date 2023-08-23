package com.coha.bjjhub.advice;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.coha.bjjhub.exception.InvalidCredentialsException;
import com.coha.bjjhub.exception.UserAlreadyExistsException;
import com.coha.bjjhub.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// @ControllerAdvice, @ExceptionHandler 어노테이션을 사용하여 전역 예외 처리기를 만들 수 있음
// 이를 통해 서비스 또는 컨트롤러에서 발생한 예외를 한 곳에서 처리할 수 있음
@Slf4j
@RestControllerAdvice // -> 모든 컨트롤러에서 발생한 예외를 핸들링할 수 있음
public class GlobalExceptionHandler {


    // @ExceptionHandler 어노테이션을 사용하면 특정 예외 유형을 처리하는 메서드를 지정할 수 있음
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        log.error("User already exists exception: {}", e.getMessage()); // 사용자가 이미 존재 예외
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
        log.error("User not found exception: {}", e.getMessage()); // 사용자를 찾을 수 없는 예외
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentialsException(InvalidCredentialsException e) {
        log.error("Invalid credentials exception: {}", e.getMessage()); // 잘못된 자격증명 예외
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<String> handleJwtVerificationException(JWTVerificationException e) {
        log.error("JWT Error: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        log.error("Internal server error: {}", e.getMessage()); // 내부 서버 오류
        return new ResponseEntity<>("서버 내부 오류가 발생하였습니다. 관리자에게 문의해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}