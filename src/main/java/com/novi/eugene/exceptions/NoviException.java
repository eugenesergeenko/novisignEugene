package com.novi.eugene.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NoviException extends RuntimeException {
    private final HttpStatus status;

    public NoviException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
