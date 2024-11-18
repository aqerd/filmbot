package org.oopproject.validators;

public interface Validator<T> {
    boolean isValid(T input);
    String getErrorMessage();
}

