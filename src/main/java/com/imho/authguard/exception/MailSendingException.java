package com.imho.authguard.exception;

import org.springframework.http.HttpStatus;

public class MailSendingException extends GlobalException {
    public MailSendingException(String title, String message, HttpStatus statusCode) {
        super(title, message, statusCode);
    }

    public MailSendingException(String title, String message, HttpStatus statusCode, Throwable cause) {
        super(title, message, statusCode, cause);
    }
}
