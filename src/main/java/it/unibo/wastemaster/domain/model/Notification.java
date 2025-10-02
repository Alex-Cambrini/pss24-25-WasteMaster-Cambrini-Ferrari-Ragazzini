package it.unibo.wastemaster.domain.model;

import java.time.LocalDateTime;

/**
 * Represents a notification with a message and a timestamp.
 * Instances are immutable.
 */
public class Notification {

    /** The content of the notification. */
    private final String message;

    /** The timestamp when the notification was created. */
    private final LocalDateTime timestamp;

    /**
     * Creates a new notification with the given message.
     * The timestamp is set to the current date and time.
     *
     * @param message the notification message
     */
    public Notification(final String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Creates a new notification with the given message and timestamp.
     *
     * @param message the notification message
     * @param timestamp the time of the notification
     */
    public Notification(final String message, final LocalDateTime timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    /**
     * Returns the notification message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the timestamp of the notification.
     *
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Returns a string representation of the notification,
     * formatted as "[timestamp] message".
     *
     * @return formatted string
     */
    @Override
    public String toString() {
        return "[" + timestamp + "] " + message;
    }
}
