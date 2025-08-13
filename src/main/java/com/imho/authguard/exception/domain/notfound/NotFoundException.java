package com.imho.authguard.exception.domain.notfound;

import com.imho.authguard.exception.domain.DomainException;

public class NotFoundException extends DomainException {

    public NotFoundException(String title, String message) {
        super(title, message);
    }

    public NotFoundException(String title, String message, String solution) {
        super(title, message, solution);
    }

}
