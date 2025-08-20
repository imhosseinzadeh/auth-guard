package com.imho.authguard.exception.domain.confilict;

import com.imho.authguard.exception.domain.DomainException;

public class ConflictException extends DomainException {

    public ConflictException(String message) {
        super(message);
    }

}
