package com.imho.authguard.exception.domain.expired;

public class ExpiredOtpException extends ExpiredException {

    public ExpiredOtpException(String title, String message) {
        super(title, message);
    }

    public ExpiredOtpException(String title, String message, String solution) {
        super(title, message, solution);
    }

}
