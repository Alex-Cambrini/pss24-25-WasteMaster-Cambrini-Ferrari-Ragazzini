package it.unibo.wastemaster.core.utils;

public class ValidateUtils {
    public static void validateString(String toValidate) {
        if (toValidate == null || toValidate.isBlank()) {
            throw new IllegalArgumentException("The string cannot be null or empty");
        }
    }
}
