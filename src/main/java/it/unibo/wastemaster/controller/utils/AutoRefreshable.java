package it.unibo.wastemaster.controller.utils;

/**
 * Interface for controllers that support automatic data refresh.
 * Implementing classes should provide logic to start and stop periodic refresh operations.
 */
public interface AutoRefreshable {
    /**
     * Starts the automatic refresh process.
     */
    void startAutoRefresh();

    /**
     * Stops the automatic refresh process.
     */
    void stopAutoRefresh();
}
