package com.gi.authservice.web;

import com.gi.authservice.exception.AccountLockedException;
import com.gi.authservice.exception.InvalidCredentialsException;
import com.gi.authservice.exception.TokenExpiredException;
import com.gi.authservice.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler({InvalidCredentialsException.class, TokenExpiredException.class})
    public ResponseEntity<Map<String, Object>> handleAuthErrors(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "timestamp", Instant.now().toString(),
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<Map<String, Object>> handleLocked(AccountLockedException ex) {
        return ResponseEntity.status(HttpStatus.LOCKED)
                .body(Map.of(
                        "timestamp", Instant.now().toString(),
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "timestamp", Instant.now().toString(),
                        "message", ex.getMessage()
                ));
    }
}
