package ru.practicum.shareit.exception;

public class NotFoundException extends RuntimeException {
    private Class clazz;

    public NotFoundException(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public String getMessage() {
        return clazz.getName();
    }
}
