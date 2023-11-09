package ru.yandex.practicum.filmorate.exception;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String s) {
        super(s);
    }
}