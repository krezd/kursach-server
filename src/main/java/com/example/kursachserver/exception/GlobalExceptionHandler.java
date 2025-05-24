package com.example.kursachserver.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handle(ResponseStatusException ex) {
        Map<String, Object> err = new HashMap<>();
        err.put("timestamp", Instant.now().toString());
        err.put("status", ex.getStatusCode().value());
        err.put("error", ex.getReason() != null ? ex.getReason() : ex.getStatusCode());
        err.put("message", ex.getReason() != null ? ex.getReason() : "Ошибка");
        return ResponseEntity.status(ex.getStatusCode()).body(err);
    }
}

