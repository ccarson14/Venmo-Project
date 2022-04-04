package com.techelevator.tenmo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "Negative value not acceptable.")
public class NegativeValueException extends Exception{

    public NegativeValueException() {
        super("Negative value not acceptable.");
    }
}
