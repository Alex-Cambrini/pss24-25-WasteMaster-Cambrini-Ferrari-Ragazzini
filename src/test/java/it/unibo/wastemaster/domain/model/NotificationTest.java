package it.unibo.wastemaster.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    void testConstructorWithMessageAndTimestamp() {
        String msg = "Test notification";
        LocalDateTime now = LocalDateTime.of(2024, 1, 1, 12, 0);
        Notification notification = new Notification(msg, now);

        assertEquals(msg, notification.getMessage());
        assertEquals(now, notification.getTimestamp());
    }

    @Test
    void testConstructorWithMessageOnly() {
        String msg = "Only message";
        Notification notification = new Notification(msg);

        assertEquals(msg, notification.getMessage());
        assertNotNull(notification.getTimestamp());
    }

    @Test
    void testToStringContainsMessageAndTimestamp() {
        String msg = "String test";
        LocalDateTime now = LocalDateTime.of(2024, 5, 10, 15, 30);
        Notification notification = new Notification(msg, now);

        String str = notification.toString();
        assertTrue(str.contains("2024-05-10T15:30"));
        assertTrue(str.contains("String test"));
    }
}