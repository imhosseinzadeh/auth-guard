package com.imho.authguard.exception;

import com.imho.authguard.dto.response.JsonResponse;
import com.imho.authguard.exception.domain.DomainException;
import com.imho.authguard.exception.domain.confilict.ConflictException;
import com.imho.authguard.exception.domain.expired.ExpiredException;
import com.imho.authguard.exception.domain.notfound.NotFoundException;
import com.imho.authguard.exception.infrastructure.InfrastructureException;
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


    @ExceptionHandler(DomainException.class)
    public ResponseEntity<JsonResponse<ProblemDetail>> handleDomainException(DomainException ex) {
        log.error("Handled DomainException: {}", ex.getMessage(), ex); // TODO AOP

        HttpStatus status = resolveHttpStatus(ex);
        ProblemDetail problem = buildProblemDetail(status, ex.getMessage());

        JsonResponse<ProblemDetail> response = new JsonResponse<>(false, ex.getMessage(), problem);

        return ResponseEntity
                .status(problem.getStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<JsonResponse<ProblemDetail>> handleValidationException(MethodArgumentNotValidException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String detail = "Validation failed";
        ProblemDetail problem = buildProblemDetail(status, detail);

        // Set validation errors
        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", Optional.ofNullable(error.getDefaultMessage()).orElse("Validation error")))
                .toList();
        problem.setProperty("errors", errors);

        JsonResponse<ProblemDetail> response = new JsonResponse<>(false, ex.getMessage(), problem);

        return ResponseEntity
                .status(status)
                .body(response);
    }

    @ExceptionHandler(InfrastructureException.class)
    public ResponseEntity<JsonResponse<ProblemDetail>> handleInfrastructureException(InfrastructureException ex) {
        log.error("Handled InfrastructureException: {}", ex.getMessage(), ex); // TODO AOP

        ProblemDetail problem = ex.getBody();

        JsonResponse<ProblemDetail> response = new JsonResponse<>(false, ex.getMessage(), problem);

        return ResponseEntity
                .status(problem.getStatus())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JsonResponse<ProblemDetail>> handleUnexpectedException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex); // TODO AOP


        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String detail = "Unexpected error occurred";

        ProblemDetail problem = buildProblemDetail(status, detail);

        JsonResponse<ProblemDetail> response = new JsonResponse<>(false, problem.getTitle(), problem);

        return ResponseEntity
                .status(status)
                .body(response);
    }

    private HttpStatus resolveHttpStatus(DomainException ex) {
        if (ex instanceof NotFoundException) return HttpStatus.NOT_FOUND;
        if (ex instanceof ConflictException) return HttpStatus.CONFLICT;
        if (ex instanceof ExpiredException) return HttpStatus.GONE;
        return HttpStatus.BAD_REQUEST;
    }

    private ProblemDetail buildProblemDetail(HttpStatus status, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setProperty("timestamp", ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
        problem.setProperty("correlationId", UUID.randomUUID().toString());
        return problem;
    }

}

