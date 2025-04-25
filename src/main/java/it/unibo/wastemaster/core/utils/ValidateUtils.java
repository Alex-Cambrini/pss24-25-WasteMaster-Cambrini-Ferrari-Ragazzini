package it.unibo.wastemaster.core.utils;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class ValidateUtils {

    public static final Validator VALIDATOR;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = factory.getValidator();
    }

    public static void validateString(String toValidate, String errorMessage) {
        if (toValidate == null || toValidate.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
    
    public static void requireArgNotNull(Object toValidate, String errorMessage) {
        if (toValidate==null) throw new IllegalArgumentException(errorMessage);
    }
    
    public static void requireStateNotNull(Object toValidate, String errorMessage) {
        if (toValidate==null) throw new IllegalStateException(errorMessage);
    }
    
}
