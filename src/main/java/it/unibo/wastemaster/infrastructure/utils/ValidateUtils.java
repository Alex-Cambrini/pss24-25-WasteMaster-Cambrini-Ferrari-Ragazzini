package it.unibo.wastemaster.infrastructure.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class for validation of entities and arguments. Provides static methods to
 * validate strings, arguments, state, and Jakarta Bean Validation for entities.
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
     * @param errorMessage the error message to throw if invalid
     * @throws IllegalArgumentException if string is null or blank
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
     * @param toValidate the object to check
     * @param errorMessage the error message to throw if null
     * @throws IllegalArgumentException if argument is null
     */
    public static void requireArgNotNull(final Object toValidate,
                                         final String errorMessage) {
        if (toValidate == null) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Validates that a state argument is not null.
     *
     * @param toValidate the object to check
     * @param errorMessage the error message to throw if null
     * @throws IllegalStateException if argument is null
     */
    public static void requireStateNotNull(final Object toValidate,
                                           final String errorMessage) {
        if (toValidate == null) {
            throw new IllegalStateException(errorMessage);
        }
    }

    /**
     * Validates the entity using Jakarta Bean Validation.
     *
     * @param <T> the entity type
     * @param entity the entity to validate
     * @throws IllegalArgumentException if entity is null or validation fails
     */
    public static <T> void validateEntity(final T entity) {
        requireArgNotNull(entity, "Entity must not be null");
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(entity);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Validation failed:");
            for (ConstraintViolation<T> violation : violations) {
                errorMessage.append(" ").append(violation.getMessage()).append(";");
            }
            throw new IllegalArgumentException(errorMessage.toString());
        }
    }

    /**
     * Validates multiple entities at once using Jakarta Bean Validation. Collects all
     * violation messages from all entities.
     *
     * @param entities the entities to validate
     * @throws IllegalArgumentException if any validation violations are found
     */
    public static void validateAll(final Object... entities) {
        LinkedHashSet<String> errorMessages = new LinkedHashSet<>();

        for (Object entity : entities) {
            Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(entity);
            for (ConstraintViolation<?> violation : violations) {
                errorMessages.add("- " + violation.getMessage());
            }
        }
        if (!errorMessages.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errorMessages));
        }
    }

    /**
     * Validates that a list is not null or empty.
     *
     * @param list the list to check
     * @param errorMessage the error message to throw if null or empty
     * @throws IllegalArgumentException if list is null or empty
     */
    public static void requireListNotEmpty(final List<?> list,
                                           final String errorMessage) {
        requireArgNotNull(list, errorMessage);
        if (list.isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
