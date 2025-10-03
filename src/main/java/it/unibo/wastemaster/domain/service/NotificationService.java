package it.unibo.wastemaster.domain.service;

import java.util.List;

/**
 * Service interface for sending notifications to recipients
 * about system events such as trip cancellations.
 */

public interface NotificationService {

    /**
     * Sends a notification about a trip cancellation to the specified recipients.
     *
     * @param recipients list of recipient email addresses
     * @param subject the subject of the notification
     * @param body the body content of the notification
     */
    void notifyTripCancellation(List<String> recipients, String subject, String body);
}
