package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException e) {
        log.warn("Error: Not Found. Message: {}", e.getMessage(), e);
        return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> parameterNotValid(final ValidationException e) {
        log.warn("Error: Bad Request. Message: {}", e.getMessage(), e);
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, String>> handleConflictException(ConflictException e) {
        log.warn("Error: Conflict. Message: {}", e.getMessage(), e);
        return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        log.error("Unexpected error occurred. Message: {}", e.getMessage(), e);
        return buildErrorResponse("Произошла непредвиденная ошибка. " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Map<String, String>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        return new ResponseEntity<>(errorResponse, status);
    }
}
