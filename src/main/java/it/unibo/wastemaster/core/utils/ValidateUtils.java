package it.unibo.wastemaster.core.utils;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
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

    public static <T> void validateEntity(T entity) {
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(entity);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<T> violation : violations) {
                throw new IllegalArgumentException(violation.getMessage());
            }
        }
    }
}
