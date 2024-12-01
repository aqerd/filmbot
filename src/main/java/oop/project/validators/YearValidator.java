package oop.project.validators;

import java.time.Year;

public class YearValidator implements Validator<String> {
    private final int minYear = 1895;
    private final int maxYear = Year.now().getValue() + 5;

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