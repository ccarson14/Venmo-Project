package com.techelevator.tenmo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "User not authorized.")
public class UserNotAuthorizedException extends Exception{

    public UserNotAuthorizedException() {
        super("User not authorized.");
    }
}
