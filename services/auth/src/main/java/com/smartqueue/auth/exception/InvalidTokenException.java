package com.smartqueue.auth.exception;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        String message = "The token has expired or is invalid. Please log in again!";
        super(message);
    }

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

}
