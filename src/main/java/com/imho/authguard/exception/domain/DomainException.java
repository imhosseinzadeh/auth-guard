package com.imho.authguard.exception.domain;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {

    private final String title;

    private final String solution;

    public DomainException(String title, String message) {
        super(message);
        this.title = title;
        this.solution = null;
    }

    public DomainException(String title, String message, String solution) {
        super(message);
        this.title = title;
        this.solution = solution;
    }

}

