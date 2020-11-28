package com.project.munchkin.user.exception;

import com.project.munchkin.base.exception.StatusException;
import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends StatusException {
    public UsernameAlreadyExistsException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
