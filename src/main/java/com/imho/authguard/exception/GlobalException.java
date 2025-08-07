package com.imho.authguard.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

import java.time.ZonedDateTime;

@Getter
public class GlobalException extends RuntimeException implements ErrorResponse {
    private final HttpStatus statusCode;
    private final ProblemDetail body;

    public GlobalException(String title, String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = statusCode;
        this.body = ProblemDetail.forStatusAndDetail(statusCode, message);
        this.body.setTitle(title);
        this.body.setProperty("timestamp", ZonedDateTime.now());
    }

    public GlobalException(String title, String message, HttpStatus statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.body = ProblemDetail.forStatusAndDetail(statusCode, message);
        this.body.setTitle(title);
        this.body.setProperty("timestamp", ZonedDateTime.now());
    }

}

