package ru.practicum.shareit.exception;

public class DuplicateValuesException extends RuntimeException {
    private Class clazz;

    public DuplicateValuesException(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public String getMessage() {
        return clazz.getName();
    }
}
