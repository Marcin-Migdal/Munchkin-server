package com.project.munchkin.playerStatus.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserNotInRoomException extends RuntimeException {
    private Long userId;
    private Long roomId;

    public UserNotInRoomException( Long userId, Long roomId) {
        super(String.format("User with id: " + userId + " is not in the room with id: " + roomId));
    }
}
