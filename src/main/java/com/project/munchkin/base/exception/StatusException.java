package com.project.munchkin.base.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class StatusException extends RuntimeException {
    private HttpStatus httpStatus;

    public StatusException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}