package oop.project.validators;

public class AgeValidator implements Validator<String> {
    @Override
    public boolean isValid(String input) {
        try {
            int age = Integer.parseInt(input);
            return age >= 0 && age <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String getErrorMessage() {
        return "Пожалуйста, введите корректное число (от 0 до 100)";
    }
}

