package com.project.munchkin.user.exception;

import com.project.munchkin.base.exception.StatusException;
import org.springframework.http.HttpStatus;

public class InGameNameAlreadyExistsException extends StatusException {
    public InGameNameAlreadyExistsException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
