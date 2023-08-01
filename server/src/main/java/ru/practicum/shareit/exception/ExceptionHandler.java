package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice("ru.practicum.shareit")
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(NotFoundException e) {
        log.warn("Получен статус 404 Not found {}", e.getMessage(), e);
        return Map.of("message", e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIncorrectArguments(IncorrectArgumentException e) {
        log.warn("Получен статус 400 Bad request {}", e.getMessage(), e);
        return Map.of("message", e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDuplicateValues(DuplicateValuesException e) {
        log.warn("Получен статус 409 Conflict {}", e.getMessage(), e);
        return Map.of("message", e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleInternalServerError(InternalServerException e) {
        log.warn("Получен статус 500 Internal Server Error {}", e.getMessage(), e);
        return Map.of("error", e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleInternalServerError(Throwable e) {
        log.warn("Получен статус 500 Internal Server Error {}", e.getMessage(), e);
        return Map.of("message", e.getMessage());
    }
}
