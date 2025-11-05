package com.smartqueue.auth.exception;

public class AuthLogInException extends RuntimeException {

    public AuthLogInException(String message) {
        super(message);
    }

    public AuthLogInException(String message, Throwable cause) {
        super(message, cause);
    }

}
