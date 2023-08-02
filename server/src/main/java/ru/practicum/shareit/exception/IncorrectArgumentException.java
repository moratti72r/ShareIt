package ru.practicum.shareit.exception;

public class IncorrectArgumentException extends RuntimeException {

    private Class clazz;

    public IncorrectArgumentException(Class clazz) {
        this.clazz = clazz;
    }

    public String getMessage() {
        return clazz.getName();
    }
}
