package ru.example.validator;

public interface Validator<T> {
    void validate(T object);
}