package com.wwme.wwme.user.exception;

public class NotAuthorizedUserException extends Throwable {
    public NotAuthorizedUserException() {
        super();
    }

    public NotAuthorizedUserException(String message) {
        super(message);
    }

    public NotAuthorizedUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAuthorizedUserException(Throwable cause) {
        super(cause);
    }
}
