package com.imho.authguard.common;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Global exception handler for REST controllers.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles global exceptions.
     *
     * @param ex The exception being handled.
     * @return An error response.
     */
    @ExceptionHandler(value = Exception.class)
    public ErrorResponse handleGlobalException(Exception ex) {
        return ErrorResponse.builder(ex, ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage())).build();
    }

    /**
     * Handles internal exceptions.
     *
     * @param ex         The exception being handled.
     * @param headers    The HTTP headers.
     * @param statusCode The HTTP status code.
     * @param request    The request that led to the exception.
     * @return An error response.
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        return ResponseEntity.status(statusCode)
                .body(ProblemDetail.forStatusAndDetail(statusCode, ex.getLocalizedMessage()));
    }

    /**
     * Handles method argument validation exceptions.
     *
     * @param ex         The exception being handled.
     * @param headers    The HTTP headers.
     * @param statusCode The HTTP status code.
     * @param request    The request that led to the exception.
     * @return An error response.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        return ResponseEntity.status(statusCode)
                .body(ProblemDetail.forStatusAndDetail(statusCode, ex.getLocalizedMessage()));
    }
}
