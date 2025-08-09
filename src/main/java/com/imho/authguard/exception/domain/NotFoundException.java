package com.imho.authguard.exception.domain;

public class NotFoundException extends DomainException {

    public NotFoundException(String title, String message) {
        super(title, message);
    }

    public NotFoundException(String title, String message, String solution) {
        super(title, message, solution);
    }

}
