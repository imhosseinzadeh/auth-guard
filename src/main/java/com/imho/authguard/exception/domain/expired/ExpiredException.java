package com.imho.authguard.exception.domain.expired;

import com.imho.authguard.exception.domain.DomainException;

public class ExpiredException extends DomainException {

    public ExpiredException(String title, String message) {
        super(title, message);
    }

    public ExpiredException(String title, String message, String solution) {
        super(title, message, solution);
    }

}
