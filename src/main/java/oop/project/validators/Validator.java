package oop.project.validators;

public interface Validator<T> {
    boolean isValid(T input);
    String getErrorMessage();
}