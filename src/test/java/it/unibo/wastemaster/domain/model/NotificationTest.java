package it.unibo.wastemaster.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class NotificationTest {

    private static final int YEAR = 2024;
    private static final int JANUARY = 1;
    private static final int MAY = 5;
    private static final int DAY_1 = 1;
    private static final int DAY_10 = 10;
    private static final int HOUR_NOON = 12;
    private static final int HOUR_15 = 15;
    private static final int MINUTE_0 = 0;
    private static final int MINUTE_30 = 30;

    @Test
    void testConstructorWithMessageAndTimestamp() {
        String msg = "Test notification";
        LocalDateTime now = LocalDateTime.of(YEAR, JANUARY, DAY_1, HOUR_NOON, MINUTE_0);
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
        LocalDateTime now = LocalDateTime.of(YEAR, MAY, DAY_10, HOUR_15, MINUTE_30);
        Notification notification = new Notification(msg, now);

        String str = notification.toString();
        assertTrue(str.contains("2024-05-10T15:30"));
        assertTrue(str.contains("String test"));
    }
}
