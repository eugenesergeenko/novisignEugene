package com.novi.eugene.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class NoviExceptionHandler {

    @ExceptionHandler(NoviException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleNoviException(NoviException e) {
        log.error("NoviException: {}", e.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("occurredAt", LocalDateTime.now());
        errorResponse.put("status", e.getStatus().value());
        errorResponse.put("error", e.getStatus().getReasonPhrase());
        errorResponse.put("message", e.getMessage());

        return Mono.just(ResponseEntity.status(e.getStatus()).body(errorResponse));
    }

    public Mono<ResponseEntity<Map<String, Object>>> handleGenericException(Exception e) {
        log.error("Unexpected app behavior, Error: {}", e.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", "Unexpected app behavior detected");

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));

    }
}
