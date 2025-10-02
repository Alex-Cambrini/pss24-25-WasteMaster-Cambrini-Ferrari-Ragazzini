package it.unibo.wastemaster.infrastructure.notification;

import it.unibo.wastemaster.domain.service.NotificationService;

import java.util.List;

public class FakeNotificationService implements NotificationService {

    @Override
    public void notifyTripCancellation(List<String> recipients, String subject, String body) {
        System.out.println("=== FakeNotificationService ===");
        System.out.println("Simulated email to: " + recipients);
        System.out.println("Subject: " + subject);
        System.out.println("Body:\n" + body);
        System.out.println("=== END ===");
    }
}
