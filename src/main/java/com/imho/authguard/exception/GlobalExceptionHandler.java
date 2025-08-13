package com.imho.authguard.exception;

import com.imho.authguard.exception.domain.DomainException;
import com.imho.authguard.exception.domain.confilict.ConflictException;
import com.imho.authguard.exception.domain.expired.ExpiredException;
import com.imho.authguard.exception.domain.notfound.NotFoundException;
import com.imho.authguard.exception.infrastructure.InfrastructureException;
import com.imho.authguard.infra.i18n.MessageResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageResolver messageResolver;

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ProblemDetail> handleDomainException(DomainException ex) {
        log.error("Handled DomainException: {}", ex.getMessage(), ex);

        HttpStatus status = resolveHttpStatus(ex);
        ProblemDetail problem = buildProblemDetail(status, ex.getTitle(), ex.getMessage());
        problem.setProperty("solution", ex.getSolution());

        return ResponseEntity
                .status(status)
                .body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(MethodArgumentNotValidException ex) {
        String title = messageResolver.getMessage("error.validation.title");
        String detail = messageResolver.getMessage("error.validation.detail");

        ProblemDetail problem = buildProblemDetail(HttpStatus.BAD_REQUEST, title, detail);
        problem.setProperty("solution", "error.validation.solution");

        // Set validation errors
        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", Optional.ofNullable(error.getDefaultMessage()).orElse("Validation error")))
                .toList();
        problem.setProperty("errors", errors);

        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(InfrastructureException.class)
    public ResponseEntity<ProblemDetail> handleInfrastructureException(InfrastructureException ex) {
        log.error("Handled InfrastructureException: {}", ex.getMessage(), ex);

        ProblemDetail problem = ex.getBody();
        problem.setProperty("solution", "error.infrastructure.solution");

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpectedException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);

        String title = messageResolver.getMessage("error.unexpected.title");
        String detail = messageResolver.getMessage("error.unexpected.detail");

        ProblemDetail problem = buildProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, title, detail);
        problem.setProperty("solution", "error.unexpected.solution");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(problem);
    }

    private HttpStatus resolveHttpStatus(DomainException ex) {
        if (ex instanceof NotFoundException) return HttpStatus.NOT_FOUND;
        if (ex instanceof ConflictException) return HttpStatus.CONFLICT;
        if (ex instanceof ExpiredException) return HttpStatus.GONE;
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private ProblemDetail buildProblemDetail(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setProperty("timestamp", ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
        problem.setProperty("correlationId", UUID.randomUUID().toString());
        return problem;
    }

}

