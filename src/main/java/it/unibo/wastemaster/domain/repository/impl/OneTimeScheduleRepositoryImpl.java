package it.unibo.wastemaster.domain.repository.impl;

import it.unibo.wastemaster.domain.model.OneTimeSchedule;
import it.unibo.wastemaster.domain.repository.OneTimeScheduleRepository;
import it.unibo.wastemaster.infrastructure.dao.OneTimeScheduleDAO;
import java.util.Optional;

/**
 * Implementation of {@link OneTimeScheduleRepository} that uses
 * {@link OneTimeScheduleDAO}
 * to perform CRUD operations on OneTimeSchedule entities.
 */
public class OneTimeScheduleRepositoryImpl implements OneTimeScheduleRepository {

    private final OneTimeScheduleDAO oneTimeScheduleDAO;

    /**
     * Constructs the repository with the specified DAO.
     *
     * @param oneTimeScheduleDAO the DAO used to access one-time schedule data
     */
    public OneTimeScheduleRepositoryImpl(final OneTimeScheduleDAO oneTimeScheduleDAO) {
        this.oneTimeScheduleDAO = oneTimeScheduleDAO;
    }

    /**
     * Persists a new one-time schedule.
     *
     * @param schedule the schedule to save
     */
    @Override
    public void save(final OneTimeSchedule schedule) {
        oneTimeScheduleDAO.insert(schedule);
    }

    /**
     * Updates an existing one-time schedule.
     *
     * @param schedule the schedule to update
     */
    @Override
    public void update(final OneTimeSchedule schedule) {
        oneTimeScheduleDAO.update(schedule);
    }

    /**
     * Retrieves a one-time schedule by its ID.
     *
     * @param id the schedule ID
     * @return an Optional containing the schedule if found, or empty
     */
    @Override
    public Optional<OneTimeSchedule> findById(final Integer id) {
        return oneTimeScheduleDAO.findById(id);
    }
}
