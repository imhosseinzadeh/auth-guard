package com.imho.authguard.exception.domain.expired;

import com.imho.authguard.exception.domain.DomainException;

public class ExpiredException extends DomainException {

    public ExpiredException(String message) {
        super(message);
    }

}
