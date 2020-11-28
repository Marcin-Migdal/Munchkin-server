package com.project.munchkin.room.exception;

import com.project.munchkin.base.exception.StatusException;
import org.springframework.http.HttpStatus;

public class NotAuthorizedException extends StatusException {
    public NotAuthorizedException(String message, HttpStatus httpStatus) {
        super("You are not authorized to %s " + message, httpStatus);
    }
}
