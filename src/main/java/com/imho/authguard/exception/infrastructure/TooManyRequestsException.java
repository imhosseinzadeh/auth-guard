package com.imho.authguard.exception.infrastructure;

public class TooManyRequestsException extends InfrastructureException {

    public TooManyRequestsException(String title, String message) {
        super(title, message);
    }

    public TooManyRequestsException(String title, String message, Throwable cause) {
        super(title, message, cause);
    }

}
