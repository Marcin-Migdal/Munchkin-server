package com.project.munchkin.playerStatus.exception;

import com.project.munchkin.base.exception.StatusException;
import org.springframework.http.HttpStatus;

public class RoomIsFullException extends StatusException {
    public RoomIsFullException(HttpStatus httpStatus) {
        super("Room is full, room creator has to make space for new players", httpStatus);
    }
}
