package it.unibo.wastemaster.domain.service;

import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.domain.model.WasteSchedule;
import it.unibo.wastemaster.domain.repository.WasteScheduleRepository;
import it.unibo.wastemaster.infrastructure.utils.ValidateUtils;
import java.time.DayOfWeek;

/**
 * Manages the scheduling of waste collections.
 */
public final class WasteScheduleManager {

    private final WasteScheduleRepository wasteScheduleRepository;

    /**
     * Constructs a WasteScheduleManager with the specified DAO.
     *
     * @param wasteScheduleRepository DAO for waste scheduling
     */
    public WasteScheduleManager(final WasteScheduleRepository wasteScheduleRepository) {
        this.wasteScheduleRepository = wasteScheduleRepository;
    }

    /**
     * Sets up a new collection routine for a specific waste type.
     *
     * @param waste the waste type
     * @param dayOfWeek the collection day
     * @return the created WasteSchedule
     */
    public WasteSchedule setupCollectionRoutine(final Waste waste,
                                                final DayOfWeek dayOfWeek) {
        WasteSchedule wasteSchedule = new WasteSchedule(waste, dayOfWeek);
        wasteScheduleRepository.save(wasteSchedule);
        return wasteSchedule;
    }

    /**
     * Changes the collection day of an existing schedule.
     *
     * @param wasteSchedule the existing schedule
     * @param newDayOfWeek the new day to assign
     * @return the updated WasteSchedule
     */
    public WasteSchedule changeCollectionDay(final WasteSchedule wasteSchedule,
                                             final DayOfWeek newDayOfWeek) {
        wasteSchedule.setDayOfWeek(newDayOfWeek);
        wasteScheduleRepository.update(wasteSchedule);
        return wasteSchedule;
    }

    /**
     * Retrieves the collection schedule for a specific waste type.
     *
     * @param waste the waste type
     * @return the associated WasteSchedule
     * @throws IllegalStateException if no schedule is found
     */
    public WasteSchedule getWasteScheduleByWaste(final Waste waste) {
        ValidateUtils.requireArgNotNull(waste, "WasteType cannot be null");
        WasteSchedule schedule = wasteScheduleRepository.findScheduleByWaste(waste);
        ValidateUtils.requireStateNotNull(schedule,
                "No WasteSchedule found for waste type: " + waste.getWasteName());
        return schedule;
    }

    /**
     * Deletes the specified waste schedule.
     *
     * @param wasteSchedule the waste schedule to delete
     */
    public void deleteSchedule(WasteSchedule wasteSchedule) {
        wasteScheduleRepository.delete(wasteSchedule);
    }
}
