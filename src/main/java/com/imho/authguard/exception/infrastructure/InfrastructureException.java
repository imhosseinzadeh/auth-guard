package com.imho.authguard.exception.infrastructure;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Getter
public class InfrastructureException extends RuntimeException implements ErrorResponse {
    private final HttpStatus statusCode;
    private final ProblemDetail body;

    public InfrastructureException(String title, String message) {
        super(message);
        this.statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        this.body = ProblemDetail.forStatusAndDetail(statusCode, message);
        this.body.setTitle(title);
        this.body.setProperty("timestamp", ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
    }

    public InfrastructureException(String title, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        this.body = ProblemDetail.forStatusAndDetail(statusCode, message);
        this.body.setTitle(title);
        this.body.setProperty("timestamp", ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
    }

}
