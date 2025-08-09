package com.imho.authguard.exception;

import com.imho.authguard.infra.i18n.MessageResolver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageResolver messageResolver;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpectedException(Exception ex, WebRequest request) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);

        // fallback for unhandled exceptions
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        detail.setTitle(messageResolver.getMessage("error.unexpected.title"));
        detail.setDetail(messageResolver.getMessage("error.unexpected.detail"));
        detail.setInstance(URI.create(request.getContextPath()));
        detail.setProperty("timestamp", ZonedDateTime.now());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(detail);
    }

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ProblemDetail> handleGlobalException(GlobalException ex) {
        log.error("Handled GlobalException: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ex.getBody());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", Optional.ofNullable(error.getDefaultMessage()).orElse("Validation error")))
                .toList();

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle(messageResolver.getMessage("error.validation.title"));
        problem.setDetail(messageResolver.getMessage("error.validation.detail"));
        problem.setProperty("errors", errors);
        problem.setProperty("timestamp", ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());

        return ResponseEntity.badRequest().body(problem);
    }

}

