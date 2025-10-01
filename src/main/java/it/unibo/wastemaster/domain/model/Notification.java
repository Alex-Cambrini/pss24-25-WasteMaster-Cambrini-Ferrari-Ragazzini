package it.unibo.wastemaster.domain.model;

import java.time.LocalDateTime;

public class Notification {
    
    private final String message;
    private final LocalDateTime timestamp;

    public Notification(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public Notification(String message, LocalDateTime timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + message;
    }
}