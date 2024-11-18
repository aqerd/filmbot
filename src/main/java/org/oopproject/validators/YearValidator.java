package org.oopproject.validators;

public class YearValidator implements Validator<String> {
    private final int minYear = 1900;
    private final int maxYear = java.time.Year.now().getValue();

    @Override
    public boolean isValid(String input) {
        try {
            int year = Integer.parseInt(input);
            return year >= minYear && year <= maxYear;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String getErrorMessage() {
        return "Пожалуйста, введите год в диапазоне от " + minYear + " до " + maxYear;
    }
}

