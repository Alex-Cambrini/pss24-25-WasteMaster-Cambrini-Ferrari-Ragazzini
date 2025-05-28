package it.unibo.wastemaster.core.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

/**
 * Utility class for validation.
 */
public final class ValidateUtils {

    /**
     * Shared Validator instance for entity validation.
     */
    public static final Validator VALIDATOR;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = factory.getValidator();
    }

    private ValidateUtils() {
        // Prevent instantiation
    }

    /**
     * Validates that a string is not null or blank.
     *
     * @param toValidate the string to validate
     * @param errorMessage the error message for exception
     */
    public static void validateString(final String toValidate,
            final String errorMessage) {
        if (toValidate == null || toValidate.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Validates that an argument is not null.
     *
     * @param toValidate the object to validate
     * @param errorMessage the error message for exception
     */
    public static void requireArgNotNull(final Object toValidate,
            final String errorMessage) {
        if (toValidate == null) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Validates that the state argument is not null.
     *
     * @param toValidate the object to validate
     * @param errorMessage the error message for exception
     */
    public static void requireStateNotNull(final Object toValidate,
            final String errorMessage) {
        if (toValidate == null) {
            throw new IllegalStateException(errorMessage);
        }
    }

    /**
     * Validates the entity using Jakarta validation.
     *
     * @param <T> the type of the entity
     * @param entity the entity to validate
     */
    public static <T> void validateEntity(final T entity) {
        requireArgNotNull(entity, "Entity must not be null");
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(entity);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<T> violation : violations) {
                throw new IllegalArgumentException(violation.getMessage());
            }
        }
    }
}
