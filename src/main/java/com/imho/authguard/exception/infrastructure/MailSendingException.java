package com.imho.authguard.exception.infrastructure;

public class MailSendingException extends InfrastructureException {

    public MailSendingException(String title, String message) {
        super(title, message);
    }

    public MailSendingException(String title, String message, Throwable cause) {
        super(title, message, cause);
    }

}
