package com.project.munchkin.playerStatus.domain;

import com.project.munchkin.base.exception.StatusException;
import org.springframework.http.HttpStatus;

public class WrongValueException extends StatusException {
    public WrongValueException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
