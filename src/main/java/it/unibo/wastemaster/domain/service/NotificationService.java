package it.unibo.wastemaster.domain.service;

import java.util.List;


public interface NotificationService {
    void notifyTripCancellation(List<String> recipients, String subject, String body);
}

