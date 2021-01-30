package com.u4.ppmtool.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserNotUniqueException extends RuntimeException {
    public UserNotUniqueException(String message) {
        super(message);
    }
}
