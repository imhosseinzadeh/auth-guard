package com.imho.authguard.exception.domain;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

}

