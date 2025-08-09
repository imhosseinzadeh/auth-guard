package com.imho.authguard.exception.domain.expired;

public class ExpiredAuthCodeException extends ExpiredException {

    public ExpiredAuthCodeException(String title, String message) {
        super(title, message);
    }

    public ExpiredAuthCodeException(String title, String message, String solution) {
        super(title, message, solution);
    }

}
