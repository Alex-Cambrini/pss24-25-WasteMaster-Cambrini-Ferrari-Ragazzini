package it.unibo.wastemaster.domain.repository.impl;

import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.domain.model.WasteSchedule;
import it.unibo.wastemaster.domain.repository.WasteScheduleRepository;
import it.unibo.wastemaster.infrastructure.dao.WasteScheduleDAO;
import java.util.Objects;

/**
 * Implementation of {@link WasteScheduleRepository} that uses {@link WasteScheduleDAO}
 * to perform CRUD operations on WasteSchedule entities.
 */
public class WasteScheduleRepositoryImpl implements WasteScheduleRepository {

    private final WasteScheduleDAO wasteScheduleDAO;

    /**
     * Constructs the repository with the specified DAO.
     *
     * @param wasteScheduleDAO the DAO used to access waste schedule data
     */
    public WasteScheduleRepositoryImpl(final WasteScheduleDAO wasteScheduleDAO) {
        this.wasteScheduleDAO = Objects.requireNonNull(wasteScheduleDAO);
    }

    /**
     * Retrieves the schedule associated with a specific waste.
     *
     * @param waste the waste to find the schedule for
     * @return the corresponding WasteSchedule
     */
    @Override
    public WasteSchedule findScheduleByWaste(final Waste waste) {
        return wasteScheduleDAO.findSchedulebyWaste(waste);
    }

    /**
     * Persists a new waste schedule.
     *
     * @param schedule the schedule to save
     * @return the saved schedule
     */
    @Override
    public WasteSchedule save(final WasteSchedule schedule) {
        wasteScheduleDAO.insert(schedule);
        return schedule;
    }

    /**
     * Updates an existing waste schedule.
     *
     * @param schedule the schedule to update
     * @return the updated schedule
     */
    @Override
    public WasteSchedule update(final WasteSchedule schedule) {
        wasteScheduleDAO.update(schedule);
        return schedule;
    }

    /**
     * Deletes a waste schedule.
     *
     * @param schedule the schedule to delete
     */
    @Override
    public void delete(final WasteSchedule schedule) {
        wasteScheduleDAO.delete(schedule);
    }
}
