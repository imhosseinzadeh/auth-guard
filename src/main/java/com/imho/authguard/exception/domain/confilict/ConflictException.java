package com.imho.authguard.exception.domain.confilict;

import com.imho.authguard.exception.domain.DomainException;

public class ConflictException extends DomainException {

    public ConflictException(String title, String message) {
        super(title, message);
    }

    public ConflictException(String title, String message, String solution) {
        super(title, message, solution);
    }

}
