package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public RuntimeException notFound(final NotFoundException e) {
        return new RuntimeException(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RuntimeException parameterNotValid(final ValidationException e) {
        return new RuntimeException(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public RuntimeException conflictException(final ConflictException e) {
        return new RuntimeException(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RuntimeException throwable(final Throwable e) {
        return new RuntimeException("Произошла непредвиденная ошибка. " + e.getMessage());
    }
}
