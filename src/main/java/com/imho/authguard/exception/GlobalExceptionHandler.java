package com.imho.authguard.exception;

import com.imho.authguard.dto.response.JsonResponse;
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
    public ResponseEntity<JsonResponse<ProblemDetail>> handleDomainException(DomainException ex) {
        log.error("Handled DomainException: {}", ex.getMessage(), ex); // TODO AOP

        HttpStatus status = resolveHttpStatus(ex);
        ProblemDetail problem = buildProblemDetail(status, ex.getTitle(), ex.getMessage());

        JsonResponse<ProblemDetail> response = new JsonResponse<>(false, problem.getTitle(), problem);

        return ResponseEntity
                .status(problem.getStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<JsonResponse<ProblemDetail>> handleValidationException(MethodArgumentNotValidException ex) {
        String title = messageResolver.getMessage("exception.validation.title");
        String detail = messageResolver.getMessage("exception.validation.detail");

        ProblemDetail problem = buildProblemDetail(HttpStatus.BAD_REQUEST, title, detail);

        // Set validation errors
        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", Optional.ofNullable(error.getDefaultMessage()).orElse("Validation error")))
                .toList();
        problem.setProperty("errors", errors);

        JsonResponse<ProblemDetail> response = new JsonResponse<>(false, problem.getTitle(), problem);

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(InfrastructureException.class)
    public ResponseEntity<JsonResponse<ProblemDetail>> handleInfrastructureException(InfrastructureException ex) {
        log.error("Handled InfrastructureException: {}", ex.getMessage(), ex); // TODO AOP

        ProblemDetail problem = ex.getBody();

        JsonResponse<ProblemDetail> response = new JsonResponse<>(false, problem.getTitle(), problem);

        return ResponseEntity
                .status(problem.getStatus())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JsonResponse<ProblemDetail>> handleUnexpectedException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex); // TODO AOP

        String title = messageResolver.getMessage("exception.unexpected.title");
        String detail = messageResolver.getMessage("exception.unexpected.detail");

        ProblemDetail problem = buildProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, title, detail);

        JsonResponse<ProblemDetail> response = new JsonResponse<>(false, problem.getTitle(), problem);

        return ResponseEntity
                .status(problem.getStatus())
                .body(response);
    }

    private HttpStatus resolveHttpStatus(DomainException ex) {
        if (ex instanceof NotFoundException) return HttpStatus.NOT_FOUND;
        if (ex instanceof ConflictException) return HttpStatus.CONFLICT;
        if (ex instanceof ExpiredException) return HttpStatus.GONE;
        return HttpStatus.BAD_REQUEST;
    }

    private ProblemDetail buildProblemDetail(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setProperty("timestamp", ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
        problem.setProperty("correlationId", UUID.randomUUID().toString());
        return problem;
    }

}

