package it.unibo.wastemaster.domain.exception;

/**
 * Exception thrown when an error occurs during the creation of an account.
 */
public class AccountCreationException extends RuntimeException {

    /**
     * Creates a new exception with the specified detail message.
     *
     * @param message the detail message describing the cause of the exception
     */
    public AccountCreationException(final String message) {
        super(message);
    }

    /**
     * Creates a new exception with the specified detail message and cause.
     *
     * @param message the detail message describing the cause of the exception
     * @param cause the underlying cause of the exception (can be {@code null})
     */
    public AccountCreationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
