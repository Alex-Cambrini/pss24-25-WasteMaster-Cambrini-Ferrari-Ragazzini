package it.unibo.wastemaster.infrastructure.notification;

import it.unibo.wastemaster.domain.service.NotificationService;
import java.util.List;

/**
 * Fake implementation of {@link NotificationService} used for testing or
 * development purposes.
 * <p>
 * Instead of sending real notifications, it simply prints the notification
 * details to the console.
 */
public final class FakeNotificationService implements NotificationService {

    /**
     * Simulates the sending of a trip cancellation notification by printing
     * the details to the console.
     *
     * @param recipients the list of email addresses of the recipients
     * @param subject the subject of the notification
     * @param body the body content of the notification
     */
    @Override
    public void notifyTripCancellation(
            final List<String> recipients,
            final String subject,
            final String body) {
        System.out.println("=== FakeNotificationService ===");
        System.out.println("Simulated email to: " + recipients);
        System.out.println("Subject: " + subject);
        System.out.println("Body:\n" + body);
        System.out.println("=== END ===");
    }
}
