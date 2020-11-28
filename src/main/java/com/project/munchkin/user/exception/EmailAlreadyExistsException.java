package com.project.munchkin.user.exception;

import com.project.munchkin.base.exception.StatusException;
import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends StatusException {
    public EmailAlreadyExistsException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
