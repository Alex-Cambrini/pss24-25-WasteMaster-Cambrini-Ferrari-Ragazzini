package it.unibo.wastemaster.domain.service;

import it.unibo.wastemaster.core.dao.WasteScheduleDAO;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.domain.model.WasteSchedule;
import java.time.DayOfWeek;

/**
 * Manages the scheduling of waste collections.
 */
public final class WasteScheduleManager {

    private final WasteScheduleDAO wasteScheduleDAO;

    /**
     * Constructs a WasteScheduleManager with the specified DAO.
     *
     * @param wasteScheduleDAO DAO for waste scheduling
     */
    public WasteScheduleManager(final WasteScheduleDAO wasteScheduleDAO) {
        this.wasteScheduleDAO = wasteScheduleDAO;
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
        wasteScheduleDAO.insert(wasteSchedule);
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
        wasteScheduleDAO.update(wasteSchedule);
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
        WasteSchedule schedule = wasteScheduleDAO.findSchedulebyWaste(waste);
        ValidateUtils.requireStateNotNull(schedule,
                "No WasteSchedule found for waste type: " + waste.getWasteName());
        return schedule;
    }
}
