package com.project.munchkin.room.exception;

import com.project.munchkin.base.exception.StatusException;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends StatusException {

    public ResourceNotFoundException(String message, HttpStatus httpStatus) {
        super(String.format(message), httpStatus);
    }

    public ResourceNotFoundException( String resourceName, String fieldName, Object fieldValue, HttpStatus httpStatus) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue), httpStatus);
    }
}
