package it.unibo.wastemaster.core.utils;

public class ValidateUtils {
    public static void validateString(String toValidate, String errorMessage) {
        if (toValidate == null || toValidate.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}