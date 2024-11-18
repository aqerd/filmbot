package oop.project.validators;

public class IdValidator implements Validator<String> {
    @Override
    public boolean isValid(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String getErrorMessage() {
        return "Пожалуйста, введите корректный ID!";
    }
}

