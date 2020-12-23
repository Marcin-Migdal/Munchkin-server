package com.project.munchkin.playerStatus.exception;

import com.project.munchkin.base.exception.StatusException;
import org.springframework.http.HttpStatus;

public class UserAlreadyInRoomException extends StatusException {
    public UserAlreadyInRoomException(Long userId, HttpStatus httpStatus) {
        super(String.format("User is already in this or another room"), httpStatus);
    }
}