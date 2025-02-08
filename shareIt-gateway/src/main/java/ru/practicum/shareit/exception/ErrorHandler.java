package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public RuntimeException notFound(final NotFoundException e) {
        log.warn("Error: Not Found. Message: {}", e.getMessage(), e);
        return new RuntimeException(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RuntimeException parameterNotValid(final ValidationException e) {
        log.warn("Error: Bad Request. Message: {}", e.getMessage(), e);
        return new RuntimeException(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public RuntimeException conflictException(final ConflictException e) {
        log.warn("Error: Conflict. Message: {}", e.getMessage(), e);
        return new RuntimeException(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RuntimeException exception(final Exception e) {
        log.error("Unexpected error occurred. Message: {}", e.getMessage(), e);
        return new RuntimeException("Произошла непредвиденная ошибка. " + e.getMessage());
    }
}
