package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.domain.model.WasteSchedule;

/**
 * Repository interface for managing WasteSchedule entities.
 * Provides CRUD operations and retrieval by waste type.
 */
public interface WasteScheduleRepository {

    /**
     * Retrieves the schedule associated with a specific waste type.
     *
     * @param waste the Waste entity to get the schedule for
     * @return the WasteSchedule associated with the given waste
     */
    WasteSchedule findScheduleByWaste(Waste waste);

    /**
     * Persists a new waste schedule.
     *
     * @param schedule the WasteSchedule entity to save
     * @return the saved WasteSchedule entity
     */
    WasteSchedule save(WasteSchedule schedule);

    /**
     * Updates an existing waste schedule.
     *
     * @param schedule the WasteSchedule entity to update
     * @return the updated WasteSchedule entity
     */
    WasteSchedule update(WasteSchedule schedule);

    /**
     * Deletes a waste schedule.
     *
     * @param schedule the WasteSchedule entity to delete
     */
    void delete(WasteSchedule schedule);
}
