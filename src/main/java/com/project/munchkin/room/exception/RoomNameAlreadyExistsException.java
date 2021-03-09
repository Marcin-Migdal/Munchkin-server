package com.project.munchkin.room.exception;

import com.project.munchkin.base.exception.StatusException;
import org.springframework.http.HttpStatus;

public class RoomNameAlreadyExistsException extends StatusException {
    public RoomNameAlreadyExistsException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
